package com.example.t.tplanner;

import android.content.res.AssetManager;
import android.graphics.Typeface;

public class ChangeFont {

    private AssetManager asset;

    public ChangeFont(AssetManager a) {
        asset = a;
    }

    public Typeface regular() {
        return Typeface.createFromAsset(asset, "fonts/Raleway-Regular.ttf");
    }

    public Typeface light() {
        return Typeface.createFromAsset(asset, "fonts/Raleway-Light.ttf");
    }

    public Typeface thin() {
        return Typeface.createFromAsset(asset, "fonts/Raleway-Thin.ttf");
    }

    public Typeface black() {
        return Typeface.createFromAsset(asset, "fonts/Raleway-Black.ttf");
    }

    public Typeface bold() {
        return Typeface.createFromAsset(asset, "fonts/Raleway-Bold.ttf");
    }

}
