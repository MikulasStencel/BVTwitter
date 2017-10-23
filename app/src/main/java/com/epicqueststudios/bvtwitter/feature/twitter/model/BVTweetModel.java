package com.epicqueststudios.bvtwitter.feature.twitter.model;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.epicqueststudios.bvtwitter.BVTwitterApplication;
import com.epicqueststudios.bvtwitter.interfaces.LifeSpanTweetInterface;
import com.epicqueststudios.bvtwitter.ui.activities.MainActivity;
import com.epicqueststudios.bvtwitter.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


public class BVTweetModel implements Parcelable {

    private static final String TAG = BVTweetModel.class.getSimpleName();
    protected String message;
    protected long id;
    protected long timeStamp = 0;
    protected final String raw_text;
    protected String name;
    protected String imageUrl;
    protected LifeSpanTweetInterface lifeSpan;

    @Inject
    protected MainActivity activity;
    @Inject
    protected BVTwitterApplication app;

    public BVTweetModel(String line) {
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
        } catch (NullPointerException e){
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

    protected BVTweetModel(Parcel in) {
        message = in.readString();
        id = in.readLong();
        timeStamp = in.readLong();
        raw_text = in.readString();
        name = in.readString();
        imageUrl = in.readString();
        lifeSpan = (LifeSpanTweetInterface) in.readValue(LifeSpanTweetInterface.class.getClassLoader());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tweet ");
        sb.append(getId());
        sb.append(" author: ");
        sb.append(getName());
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeLong(id);
        dest.writeLong(timeStamp);
        dest.writeString(raw_text);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeValue(lifeSpan);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BVTweetModel> CREATOR = new Parcelable.Creator<BVTweetModel>() {
        @Override
        public BVTweetModel createFromParcel(Parcel in) {
            return new BVTweetModel(in);
        }

        @Override
        public BVTweetModel[] newArray(int size) {
            return new BVTweetModel[size];
        }
    };
}