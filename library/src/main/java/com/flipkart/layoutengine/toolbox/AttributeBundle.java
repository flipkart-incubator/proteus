package com.flipkart.layoutengine.toolbox;

import android.util.AttributeSet;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kiran.kumar on 14/05/14.
 */
public class AttributeBundle implements AttributeSet
{
    private List<Pair<String,String>> bundleList = new ArrayList<Pair<String, String>>();
    private Map<String,String> bundleMap = new HashMap<String, String>();

    public AttributeBundle(List<Pair<String, String>> bundle) {
        this.bundleList = bundle;
        for (int i=0;i<bundle.size();i++)
        {
            Pair<String,String> value = bundle.get(i);
            bundleMap.put(value.first,value.second);
        }
    }

    @Override
    public int getAttributeCount() {
        return bundleList.size();
    }

    @Override
    public String getAttributeName(int index) {
        return bundleList.get(index).first;
    }

    @Override
    public String getAttributeValue(int index) {
        return bundleList.get(index).first;
    }

    @Override
    public String getAttributeValue(String namespace, String name) {
        return bundleMap.get(name);
    }

    @Override
    public String getPositionDescription() {
        return "";
    }

    @Override
    public int getAttributeNameResource(int index) {
        return 0;
    }

    @Override
    public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
        return 0;
    }

    @Override
    public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
        return false;
    }

    @Override
    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
        return 0;
    }

    @Override
    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeListValue(int index, String[] options, int defaultValue) {
        return 0;
    }

    @Override
    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
        return false;
    }

    @Override
    public int getAttributeResourceValue(int index, int defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeIntValue(int index, int defaultValue) {
        return 0;
    }

    @Override
    public int getAttributeUnsignedIntValue(int index, int defaultValue) {
        return 0;
    }

    @Override
    public float getAttributeFloatValue(int index, float defaultValue) {
        return 0;
    }

    @Override
    public String getIdAttribute() {
        return null;
    }

    @Override
    public String getClassAttribute() {
        return null;
    }

    @Override
    public int getIdAttributeResourceValue(int defaultValue) {
        return 0;
    }

    @Override
    public int getStyleAttribute() {
        return 0;
    }
}