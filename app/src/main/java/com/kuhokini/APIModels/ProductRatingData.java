package com.kuhokini.APIModels;

import java.util.List;

public class ProductRatingData {
    private int average_rating;
    private String actual_rating;
    private String rating_label;
    private int total_ratings;
    private int review_count;
    private RatingDistribution rating_distribution;
    private Review featured_review;
    private List<Review> recent_reviews;
    private int product_id;
    private String timestamp;

    public int getAverage_rating() {
        return average_rating;
    }

    public int getTotal_ratings() {
        return total_ratings;
    }

    public String getRating_label() {
        return rating_label;
    }

    public int getReview_count() {
        return review_count;
    }

    public String getActual_rating() {
        return actual_rating;
    }

    public void setActual_rating(String actual_rating) {
        this.actual_rating = actual_rating;
    }

    public RatingDistribution getRating_distribution() {
        return rating_distribution;
    }

    public Review getFeatured_review() {
        return featured_review;
    }

    public List<Review> getRecent_reviews() {
        return recent_reviews;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public class RatingDistribution {
        private int five_star;
        private int four_star;
        private int three_star;
        private int two_star;
        private int one_star;

        // Getters and Setters

        public int getFive_star() {
            return five_star;
        }

        public int getFour_star() {
            return four_star;
        }

        public int getThree_star() {
            return three_star;
        }

        public int getTwo_star() {
            return two_star;
        }

        public int getOne_star() {
            return one_star;
        }
    }

    public class Review {
        private int rating;
        private String comment;
        private String created_at;
        private String user_id;
        private String name;
        private String image;

        // Getters and Setters

        public int getRating() {
            return rating;
        }

        public String getComment() {
            return comment;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }
    }


}
