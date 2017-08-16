/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bz7v2moviereviews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Bo 
 */
public class NYTMoviesManager {

    private String urlString = "";
    private final String baseUrlString = "https://api.nytimes.com/svc/movies/v2/reviews/search.json";
    private final String apiKey = "97a32382417e4c31bdd5808b44b7a18a";
    private String searchString = "Star Wars";

    private URL url;
    private ArrayList<NYTMovieReview> movieReviews;

    public NYTMoviesManager() {
        movieReviews = new ArrayList<>();
    }

    public void load(String searchString) throws Exception {
        if (searchString == null || searchString.equals("")) {
            throw new Exception("The search string was empty.");
        }

        this.searchString = searchString;
        // create the urlString
        String encodedSearchString;
        try {
            // searchString must be URL encoded to put in URL
            encodedSearchString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw ex;
        }

        urlString = baseUrlString + "?query=" + encodedSearchString + "&api-key=" + apiKey;
        System.out.println("urlString: "+urlString);

        try {
            url = new URL(urlString);
        } catch (MalformedURLException muex) {
            throw muex;
        }

        String jsonString = "";
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString += inputLine;
            }
            in.close();
        } catch (IOException ioex) {
            throw ioex;
        }

        try {
            parseJsonNewsFeed(jsonString);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void parseJsonNewsFeed(String jsonString) throws Exception {

        // start with clean list
        movieReviews.clear();

        if (jsonString == null || jsonString.equals("")) {
            return;
        }

        JSONObject jsonObj;
        try {
            jsonObj = (JSONObject) JSONValue.parse(jsonString);
        } catch (Exception ex) {
            throw ex;
        }

        if (jsonObj == null) {
            return;
        }

        String status = "";
        try {
            status = (String) jsonObj.get("status");
        } catch (Exception ex) {
            throw ex;
        }

        if (status == null || !status.equals("OK")) {
            throw new Exception("Status returned from API was not OK.");
        }

        JSONArray results;
        try {
            results = (JSONArray) jsonObj.get("results");
        } catch (Exception ex) {
            throw ex;
        }

        for (Object result : results) {
            try {
                JSONObject story = (JSONObject) result;
                String webUrl = unescapeHtml4((String) story.getOrDefault("web_url", ""));
                String displayTitle = unescapeHtml4((String) story.getOrDefault("display_title", ""));
                String summaryShort = unescapeHtml4((String) story.getOrDefault("summary_short", ""));
                String publicationDate = unescapeHtml4((String) story.getOrDefault("publication_date", ""));
                String headline = unescapeHtml4((String) story.getOrDefault("headline", ""));
                JSONObject multimediaObj = (JSONObject) story.getOrDefault("multimedia", null);
                String mediaSrc = null;
                if (multimediaObj != null) {
                    mediaSrc = (String) multimediaObj.getOrDefault("src", null);
                }
                String mpaaRating = unescapeHtml4((String) story.getOrDefault("mpaa_rating", null));
                String byline = unescapeHtml4((String) story.getOrDefault("byline", null));
                JSONObject linkObj = (JSONObject) story.getOrDefault("link",null);
                String linkUrl = null;
                if (linkObj != null) {
                    linkUrl = unescapeHtml4((String) linkObj.getOrDefault("url", null));
                }

                
                System.out.println("displayTitle: " + displayTitle + "\n");
                System.out.println("headline: " + headline + "\n");
                System.out.println("webUrl: " + webUrl + "\n");
                System.out.println("publicationDate: " + publicationDate + "\n");
                System.out.println("------------------------------------------------------\n");

                NYTMovieReview movieReview = new NYTMovieReview(webUrl, displayTitle, 
                        headline, summaryShort, publicationDate, mediaSrc, mpaaRating, byline, linkUrl);
                movieReviews.add(movieReview);

            } catch (Exception ex) {
                throw ex;
            }

        }

    }

    public ArrayList<NYTMovieReview> getMovieReviews() {
        return movieReviews;
    }

    public int getNumNewsStories() {
        return movieReviews.size();
    }

}
