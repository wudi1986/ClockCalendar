package com.news.qidian.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ariesymark on 2015/3/25.
 */

@DatabaseTable(tableName = "tb_news_feed")
public class NewsFeed implements Serializable {

    public static final int NO_PIC = 0;
    public static final int ONE_AND_TWO_PIC = 1;
    public static final int THREE_PIC = 2;
    public static final int TIME_LINE = 3;

    public static final String COLUMN_CHANNEL_ID = "channel";
    public static final String COLUMN_NEWS_ID = "nid";
    public static final String COLUMN_UPDATE_TIME = "ptime";
    /**
     * 新闻发布时间
     */
    @DatabaseField
    private String ptime;
    /**
     * 新闻来源地址,tips:此字段要当着获取详情的id使用
     */
    @DatabaseField(id = true)
    private int nid;

    @DatabaseField
    private String url;

    @DatabaseField
    private String docid;
    /**
     * 新闻评论
     */
    @DatabaseField
    private int comment;
    /**
     * 新闻来源名称
     */
    @DatabaseField
    private String pname;
    @DatabaseField
    private String purl;
    /**
     * 新闻样式;0:没有图片,1:一张图片,2:两张图片,3:3张图片,4:900timeLine
     */
    @DatabaseField
    private int style;
    @DatabaseField
    private String title;
    @DatabaseField
    private String province;
    @DatabaseField
    private String city;
    @DatabaseField
    private String district;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> imgs;
    @DatabaseField
    private int channel;
    @DatabaseField
    private int collect;
    @DatabaseField
    private int concern;
    /**
     *  这个参数是收藏用的单张图
     */
    private String imageUrl;
    /**
     * 用户是否看过
     */
    @DatabaseField(dataType = DataType.BOOLEAN)
    private boolean isRead;
    /**
     * 用户是否删除这条收藏数据
     */
    private boolean isFavorite = false;

    @Override
    public String toString() {
        return "NewsFeed{" +
                "pubTime='" + ptime + '\'' +
                ", url='" + url + '\'' +
                ", docid='" + docid + '\'' +
                ", commentsCount='" + comment + '\'' +
                ", pubName='" + pname + '\'' +
                ", pubUrl='" + purl + '\'' +
                ", imgStyle='" + style + '\'' +
                ", title='" + title + '\'' +
                ", imgList=" + imgs +
                ", channelId='" + channel + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isRead=" + isRead +
                '}';
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getConcern() {
        return concern;
    }

    public void setConcern(int concern) {
        this.concern = concern;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
