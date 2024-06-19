/**
 * Copyright 2024 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ibm.eventautomation.demos.loosehangerjeans.generators;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Product;
import com.ibm.eventautomation.demos.loosehangerjeans.data.ProductReview;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Review;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.kafka.common.config.AbstractConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates a {@link ProductReview} event for a given product using randomly generated data.
 */
public class ProductReviewGenerator {

    /** Helper class to randomly generate the details of a product. */
    private final ProductGenerator productGenerator;

    /** Reviews for products will be chosen from this list. */
    private final List<Review> reviews;

    /** Subset of the reviews that have a size issue. */
    private final List<Review> reviewsWithSizeIssue;

    /** Products with a size issue will be chosen from this map. */
    private final Map<String, Product> productsWithSizeIssue;

    /** Formatter for event timestamps. */
    private final DateTimeFormatter timestampFormatter;

    /**
     * Ratio of product reviews with a size issue for products that are supposed to have a size issue.
     * Must be between 0.0 and 1.0.
     *
     * Setting this to 0 will mean that no review event with a size issue is generated
     *  for a given product that is supposed to have a size issue.
     * Setting this to 1 will mean that all the review events will be generated with a size issue
     *  for a given product that is supposed to have a size issue.
     */
    private final double reviewWithSizeIssueRatio;

    /**
     * Generator can simulate a source of events that offers
     *  at-least-once delivery semantics by occasionally
     *  producing duplicate messages.
     *
     * This value is the proportion of events that will be
     *  duplicated, between 0.0 and 1.0.
     *
     * Setting this to 0 will mean no events are duplicated.
     * Setting this to 1 will mean every message is produced twice.
     */
    private final double duplicatesRatio;

    /**
     * Generator can simulate a delay in events being produced
     *  to Kafka by putting a timestamp in the message payload
     *  that is earlier than the current time.
     *
     * The amount of the delay will be randomized to simulate
     *  a delay due to network or infrastructure reasons.
     *
     * This value is the maximum delay (in seconds) that it will
     *  use. (Setting this to 0 will mean all events are
     *  produced with the current time).
     */
    private final int MAX_DELAY_SECS;

    public ProductReviewGenerator(AbstractConfig config,
                                  Map<String, Product> productsWithSizeIssue) {
        this.productGenerator = new ProductGenerator(config);

        this.reviews = initReviews();
        // Set the subset of the reviews that have a size issue.
        // The size corresponds to the first characteristics in the reviews.
        this.reviewsWithSizeIssue = this.reviews.stream().filter(review -> review.getCharacteristics().get(0).hasIssue()).collect(Collectors.toList());

        this.productsWithSizeIssue = productsWithSizeIssue;

        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS_LTZ));

        this.reviewWithSizeIssueRatio = config.getDouble(DatagenSourceConfig.CONFIG_PRODUCTREVIEWS_REVIEW_WITH_SIZE_ISSUE_RATIO);

        this.duplicatesRatio = config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_PRODUCTREVIEWS);

        this.MAX_DELAY_SECS = config.getInt(DatagenSourceConfig.CONFIG_DELAYS_PRODUCTREVIEWS);
    }

    public ProductReview generate() {
        Product product = productGenerator.generate();
        return generate(product);
    }

    public ProductReview generate(final Product product) {
        Review review = productsWithSizeIssue.containsKey(product.getShortDescription()) && Generators.shouldDo(reviewWithSizeIssueRatio)
                ? Generators.randomItem(reviewsWithSizeIssue)
                : Generators.randomItem(reviews);
        return new ProductReview(timestampFormatter.format(Generators.nowWithRandomOffset(MAX_DELAY_SECS)),
                product.getShortDescription(), product.getSize(),
                review);
    }

    public List<Product> getProductsWithSizeIssue() {
        return new ArrayList<>(productsWithSizeIssue.values());
    }

    public boolean shouldDuplicate() {
        return Generators.shouldDo(duplicatesRatio);
    }

    private List<Review> initReviews() {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreSurroundingSpaces(true)
                .build();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/reviews.csv");
        Reader reader = inputStream != null ? new InputStreamReader(inputStream) : null;
        if (reader != null) {
            try (CSVParser parser = new CSVParser(reader, format)) {
                return parser.getRecords().stream().map(this::getReviewFromRecord).collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException("Error while parsing sample reviews", e);
            }
        } else {
            throw new RuntimeException("Error while reading sample reviews");
        }
    }

    private Review getReviewFromRecord(final CSVRecord record) {
        // Create the characteristics.
        List<Review.Characteristic> characteristics = new ArrayList<>();
        characteristics.add(new Review.Characteristic("Size", Integer.parseInt(record.get("Size"))));
        characteristics.add(new Review.Characteristic("Length", Integer.parseInt(record.get("Length"))));
        // Create the review.
        return new Review(Integer.parseInt(record.get("Rating")), record.get("Comment"), characteristics);
    }
}
