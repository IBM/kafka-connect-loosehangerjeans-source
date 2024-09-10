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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates a {@link ProductReview} event for a given product using randomly generated data.
 */
public class ProductReviewGenerator extends Generator<ProductReview> {

    /** Helper class to randomly generate the details of a product. */
    private final ProductGenerator productGenerator;

    /** Reviews for products will be chosen from this list. */
    private final List<Review> reviews;

    /** Subset of the reviews that have a size issue. */
    private final List<Review> reviewsWithSizeIssue;

    /** Products with a size issue will be chosen from this map. */
    private final Map<String, Product> productsWithSizeIssue;

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


    public ProductReviewGenerator(AbstractConfig config,
                                  Map<String, Product> productsWithSizeIssue)
    {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_PRODUCTREVIEWS),
              config.getInt(DatagenSourceConfig.CONFIG_DELAYS_PRODUCTREVIEWS),
              config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_PRODUCTREVIEWS),
              config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS_LTZ));

        this.productGenerator = new ProductGenerator(config);

        this.reviews = initReviews();
        // Set the subset of the reviews that have a size issue.
        // The size corresponds to the first characteristics in the reviews.
        this.reviewsWithSizeIssue = this.reviews.stream().filter(review -> review.getCharacteristics().get(0).hasIssue()).collect(Collectors.toList());

        this.productsWithSizeIssue = productsWithSizeIssue;

        this.reviewWithSizeIssueRatio = config.getDouble(DatagenSourceConfig.CONFIG_PRODUCTREVIEWS_REVIEW_WITH_SIZE_ISSUE_RATIO);
    }

    @Override
    protected ProductReview generateEvent(ZonedDateTime timestamp) {
        Product product = productGenerator.generate();
        return generate(product, timestamp);
    }

    public ProductReview generate(final Product product, final ZonedDateTime timestamp) {
        Review review = productsWithSizeIssue.containsKey(product.getShortDescription()) && Generators.shouldDo(reviewWithSizeIssueRatio)
                ? Generators.randomItem(reviewsWithSizeIssue)
                : Generators.randomItem(reviews);
        return new ProductReview(formatTimestamp(timestamp),
                                 product.getShortDescription(), product.getSize(),
                                 review,
                                 timestamp);
    }

    public List<Product> getProductsWithSizeIssue() {
        return new ArrayList<>(productsWithSizeIssue.values());
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
