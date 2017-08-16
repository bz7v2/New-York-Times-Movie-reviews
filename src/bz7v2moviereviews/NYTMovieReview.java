/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bz7v2moviereviews;

/**
 *
 * @author Bo 
 */
public class NYTMovieReview {
    public String webUrl;
    public String displayTitle;
    public String headline;
    public String summaryShort;
    public String publicationDate;
    public String mediaSrc;
    public String mpaaRating;
    public String byline;
    public String linkUrl;

    public NYTMovieReview(String webUrl, String displayTitle, String headline, 
            String summaryShort, String publicationDate, String mediaSrc, String mpaaRating, String byline, String linkUrl) {
        this.webUrl = webUrl;
        this.displayTitle = displayTitle;
        this.headline = headline;
        this.summaryShort = summaryShort;
        this.publicationDate = publicationDate;
        this.mediaSrc = mediaSrc;
        this.mpaaRating = mpaaRating;
        this.byline = byline;
        this.linkUrl = linkUrl;
    }
}
