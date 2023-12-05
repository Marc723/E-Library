package com.marco.e_library;

import android.os.Parcel;
import android.os.Parcelable;

public class Buku implements Parcelable {
    private String id;
    private String name;
    private String description;
    private String photoUrl;
    private String authors;


    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getName(){return name;}

    public void setName(String name){this.name = name;}

    public String getDescription(){return description;}

    public void setDescription(String description){
        this.description = description;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }


    @Override
    public int describeContents(){
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.photoUrl);
        dest.writeString(this.authors);
    }

    public Buku(){

    }
    private Buku(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.photoUrl = in.readString();
        this.authors = in.readString();
    }

    public static final Creator<Buku> CREATOR =
            new Creator<Buku>(){
                @Override
                public Buku createFromParcel(Parcel source){
                    return new Buku(source);
                }
                @Override
                public Buku[] newArray(int size){
                    return new Buku[size];
                }
            };
    }