package dev.tomco.securedsharedpreferenceslib;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SecuredSharedPreferencesManager {

    private static volatile SecuredSharedPreferencesManager instance = null;
    private SharedPreferences sharedPref;

    // MARK: Constructors
    private SecuredSharedPreferencesManager(Context context, Boolean secured) {
        this(context, secured, secured ? "APP_SP_DB_SECURED" : "APP_SP_DB");
    }

    private SecuredSharedPreferencesManager(Context context, Boolean secured, String sharedPreferencesName) {
        if (secured) {
            MasterKey masterKey = null;

            try {
                masterKey = new MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                sharedPref = EncryptedSharedPreferences.create(
                        context,
                        sharedPreferencesName,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );

            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else
            this.sharedPref = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
    }

    // MARK: initializers
    public static SecuredSharedPreferencesManager init(Context context, Boolean secured) {
        if (instance == null) {
            synchronized (SecuredSharedPreferencesManager.class) {
                if (instance == null) {
                    instance = new SecuredSharedPreferencesManager(context, secured);
                }
            }
        }
        return getInstance();
    }

    public static SecuredSharedPreferencesManager init(Context context, Boolean secured, String sharedPreferencesName) {
        if (instance == null) {
            synchronized (SecuredSharedPreferencesManager.class) {
                if (instance == null) {
                    instance = new SecuredSharedPreferencesManager(context, secured, sharedPreferencesName);
                }
            }
        }
        return getInstance();
    }

    // MARK: getInstance
    public static SecuredSharedPreferencesManager getInstance() {
        return instance;
    }

    // MARK: Put Methods
    public void putBoolean(String key, boolean value) {
        sharedPref.edit().putBoolean(key, value).apply();
    }

    public void putInt(String key, int value) {
        sharedPref.edit().putInt(key, value).apply();
    }

    public void putString(String key, String value) {
        sharedPref.edit().putString(key, value).apply();
    }

    public void putFloat(String key, float value) {
        sharedPref.edit().putFloat(key, value).apply();
    }

    public void putLong(String key, long value) {
        sharedPref.edit().putLong(key, value).apply();
    }

    public void putDouble(String key, double value) {
        sharedPref.edit().putString(key, String.valueOf(value)).apply();
    }

    public void putShort(String key, short value) {
        sharedPref.edit().putString(key, String.valueOf(value)).apply();
    }

    public void putByte(String key, byte value) {
        sharedPref.edit().putString(key, String.valueOf(value)).apply();
    }

    public void putChar(String key, char value) {
        sharedPref.edit().putInt(key, value).apply();
    }

    public void putObject(String key, Object value){
        sharedPref.edit().putString(key, new Gson().toJson(value)).apply();
    }
    public <T> void putArray(String key, ArrayList<T> array){
        String json = new Gson().toJson(array);
        sharedPref.edit().putString(key,json).apply();
    }
    public <S,T> void putMap(String key, HashMap<S,T> map){
        String json = new Gson().toJson(map);
        sharedPref.edit().putString(key,json).apply();
    }

    // MARK: Get Methods
    public boolean getBoolean(String key, boolean defValue) {
        return sharedPref.getBoolean(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sharedPref.getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sharedPref.getString(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return sharedPref.getFloat(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return sharedPref.getLong(key, defValue);
    }

    public double getDouble(String key, double defValue) {
        return Double.parseDouble(sharedPref.getString(key, String.valueOf(defValue)));
    }

    public short getShort(String key, short defValue) {
        return Short.parseShort(sharedPref.getString(key, String.valueOf(defValue)));
    }

    public byte getByte(String key, byte defValue) {
        return Byte.parseByte(sharedPref.getString(key, String.valueOf(defValue)));
    }

    public char getChar(String key, char defValue) {
        return (char)sharedPref.getInt(key, defValue);
    }

    public <T> T getObject(String key, Class<T> objectClass){
        Object object = null;
        try {
            object = new Gson().fromJson(sharedPref.getString(key, ""), objectClass);
        }catch (Exception e){
            Log.e(e.getMessage(), Arrays.stream(e.getStackTrace()).map(Object::toString).toString());
        }
        return Primitives.wrap(objectClass).cast(object);
    }
    public <T> ArrayList<T> getArray(String key, TypeToken<T> typeToken){
        ArrayList<T> arrayList = null;
        try {
            arrayList = new Gson().fromJson(sharedPref.getString(key, ""), typeToken.getType());
        }catch (Exception e){
            Log.e(e.getMessage(), Arrays.stream(e.getStackTrace()).map(Object::toString).toString());
        }
        return arrayList;
    }
    public <S,T> HashMap<S,T> getMap(String key, TypeToken<T> typeToken){
        HashMap<S,T> map = null;
        try {
            map = new Gson().fromJson(sharedPref.getString(key, ""), typeToken.getType());
        }catch (Exception e){
            Log.e(e.getMessage(), Arrays.stream(e.getStackTrace()).map(Object::toString).toString());
        }
        return map;
    }

    // MARK: Other Methods
    public boolean contains(String key){
        return sharedPref.contains(key);
    }

    public void removeKey(String key){
        sharedPref.edit().remove(key).apply();
    }
}
