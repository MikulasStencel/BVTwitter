package com.epicqueststudios.bvtwitter.model;

import android.util.Log;

import com.epicqueststudios.bvtwitter.interfaces.LifeSpanTweetInterface;
import com.epicqueststudios.bvtwitter.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class BVTweet{

    private static final String TAG = BVTweet.class.getSimpleName();
    protected String message;
    protected long id;
    protected long timeStamp = 0;
    protected final String raw_text;
    protected String name;
    protected String imageUrl;
    protected LifeSpanTweetInterface lifeSpan;

    public BVTweet(String line) {
        this.raw_text = line;
        JSONObject json;
        try {
            json = new JSONObject(line);
            this.message = json.optString("text");
            this.id = json.optLong("id");
            String time = json.optString("timestamp_ms");
            this.timeStamp = (time != null && !time.isEmpty()) ? Long.parseLong(time) : 0;
            JSONObject user = json.optJSONObject("user");
            this.name = (user != null) ? user.optString("screen_name") : "";
            this.imageUrl = (user != null) ? user.optString("profile_image_url_https") : "";
        } catch (JSONException e) {
        } catch (NumberFormatException e){
        }catch (NullPointerException e){
        }
    }

    public String getTweetTitle() {
        if (timeStamp == 0){
            return name;
        }
        return DateUtils.formatDate(timeStamp).concat("     ").concat(name);
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getMessage() {
        return message;
    }
    public boolean hasMessage() {
        return message!=null && !message.isEmpty();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getLifeSpanType() {
        if (lifeSpan == null)
            return 0;
        return lifeSpan.getID();
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean useStorage() {
        return true;
    }

    public String getRaw() {
        return raw_text;
    }

    public void setLifeSpan(LifeSpanTweetInterface lifespan){
        this.lifeSpan = lifespan;
    }

    public boolean isExpired(long now){
        if (lifeSpan == null)
            return false;
        return lifeSpan.isExpired(this, now);
    }
}
