package com.news.qidian.net;

/**
 * Created by fengjigang on 15/1/6.
 */
public abstract class FileCallback extends AbstractCallBack<String>{
    @Override
    protected String bindData(String content) {
        return content;
    }
}
