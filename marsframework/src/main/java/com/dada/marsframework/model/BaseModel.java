package com.dada.marsframework.model;


import com.dada.marsframework.serializer.*;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by laidayuan on 2018/1/29.
 */

public class BaseModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    protected static IParser mParser = new DefaultParser();
    private static ISerializer mSerializer = new DefaultSerializer();

    /**
     * Parse the json object by parser.
     *
     * @param jsonObj object be parsed.
     */
    public void parseJSONObject(JSONObject jsonObj) {
        mParser.parseJSONObject(jsonObj, this);
    }

    /**
     * Parse the json object by parser.
     *
     * @param json object be parsed.
     */
    public void parseJSONString(String json) {
        mParser.parseJSONString(json, this);
    }

    /**
     * Serialize the entity to json object.
     *
     * @return Json object
     */
    public JSONObject serializeToJSONObject() {
        return mSerializer.serializeToJSONObject(this);
    }

    /**
     * Serialize the entity to string.
     *
     * @return Json object
     */
    public String serializeToString() {
        return mSerializer.serializeToString(this);
    }

    /**
     * Set the parser.
     *
     * @param parser the strategy.
     */
    public void setParser(IParser parser) {
        if (null == parser) {
            return;
        }
        this.mParser = parser;
    }

    /**
     * Set the serializer.
     *
     * @param serializer the strategy.
     */
    public void setSerializer(ISerializer serializer) {
        if (null == serializer) {
            return;
        }
        this.mSerializer = serializer;
    }

}
