package com.github.michaelwuensch.avathorlibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import java.io.IOException;
import java.util.ArrayList;

public class AvathorFactory {

    public static Bitmap getAvathor(Context context, String input, AvatarSet set) {
        input = input.toLowerCase();
        byte[] hash = Utils.sha256Hash(input);
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        String[] bitmapPaths = getBitmapPaths(context, hash, set);

        for (String bitmapPath : bitmapPaths) {
            bitmaps.add(Utils.getBitmapFromAsset(context, bitmapPath));
        }

        return Utils.combineBitmaps(bitmaps);
    }

    private static String[] getBitmapPaths(Context context, byte[] hash, AvatarSet set) {
        String subsetPath = getSubsetPath(context, hash, set);
        String[] componentPaths = getComponentPaths(context, hash, subsetPath);
        String[] result = new String[componentPaths.length + 1];
        result[0] = getBackgroundPath(context, hash);
        System.arraycopy(componentPaths, 0, result, 1, componentPaths.length);

        return result;
    }

    private static String getBackgroundPath(Context context, byte[] hash) {
        AssetManager assetManager = context.getAssets();
        String backgroundsDirectory = "images/backgrounds";

        try {
            String[] bgSets = assetManager.list(backgroundsDirectory);
            int setIndex = (hash[0] & 0xff);
            int finalSetIndex = setIndex % bgSets.length;

            String[] backgrounds = assetManager.list(backgroundsDirectory + "/" + bgSets[finalSetIndex]);
            int backgroundIndex = (hash[1] & 0xff);
            int finalBackgroundIndex = backgroundIndex % backgrounds.length;

            return backgroundsDirectory + "/" + bgSets[finalSetIndex] + "/" + backgrounds[finalBackgroundIndex];
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String getSubsetPath(Context context, byte[] hash, AvatarSet set) {
        AssetManager assetManager = context.getAssets();
        String setsDirectory = "images/sets";
        try {
            String setDirectory = "";
            switch (set) {
                case MIXED:
                    String[] sets = assetManager.list(setsDirectory);
                    int setIndex = (hash[2] & 0xff);
                    int finalSetIndex = setIndex % sets.length;
                    setDirectory = setsDirectory + "/" + sets[finalSetIndex];
                    break;
                case ROBOTS:
                    setDirectory = setsDirectory + "/robots";
                    break;
                case ALIENS:
                    setDirectory = setsDirectory + "/aliens";
                    break;
                case HUMANS:
                    setDirectory = setsDirectory + "/humans";
                    break;
                case ANIMALS:
                    setDirectory = setsDirectory + "/animals";
                    break;
            }

            String[] subsets = assetManager.list(setDirectory);
            int subsetIndex = (hash[3] & 0xff);
            int finalSubsetIndex = subsetIndex % subsets.length;

            return setDirectory + "/" + subsets[finalSubsetIndex];

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String[] getComponentPaths(Context context, byte[] hash, String subSetPath) {
        AssetManager assetManager = context.getAssets();
        String[] componentPaths = new String[0];

        try {
            String[] componentCategories = assetManager.list(subSetPath);
            componentPaths = new String[componentCategories.length];
            for (int i = 0; i < Math.min(componentCategories.length, 13); i++) {
                String[] components = assetManager.list(subSetPath + "/" + componentCategories[i]);
                int componentIndex = ((hash[4 + i * 2] & 0xff) << 8) | (hash[4 + i * 2 + 1] & 0xff);
                int finalComponentIndex = componentIndex % components.length;
                componentPaths[i] = subSetPath + "/" + componentCategories[i] + "/" + components[finalComponentIndex];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return componentPaths;
    }

    public enum AvatarSet {
        MIXED,
        ROBOTS,
        HUMANS,
        ALIENS,
        ANIMALS;
    }
}
