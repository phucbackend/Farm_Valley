package com.example.final_project;

public class Item {
    String name, category, photo;
    String id ;
    int price, quantity , sellPrice;
    int idPhoto  , idPhotoEarly;
    int exp ;

    public int getIdPhotoEarly() {
        return idPhotoEarly;
    }

    public void setIdPhotoEarly(int idPhotoEarly) {
        this.idPhotoEarly = idPhotoEarly;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void setIdPhoto(int idPhoto) {
        this.idPhoto = idPhoto;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

//    public Item(String name, String category, String photo, String id, int price, int quantity ) {
//        this.name = name;
//        this.category = category;
//        this.id = id;
//        this.photo = photo;
//        this.price = price;
//        this.quantity = quantity ;
//    }
    public Item(String name ,int quantity , int idPhoto){
        this.name = name ;
        this.quantity = quantity ;
        this.idPhoto = idPhoto ;
    }

    public Item(String id , String name, int quantity , int idPhoto , int sellPrice , String category, int exp){
        this.id = id;
        this.name = name ;
        this.quantity = quantity ;
        this.idPhoto = idPhoto ;
        this.sellPrice = sellPrice ;
        this.category = category ;
        this.exp = exp ;
    }
    public Item(String id , String name, int quantity , int idPhoto , int sellPrice , String category, int exp,int idPhotoEarly){
        this.id = id;
        this.name = name ;
        this.quantity = quantity ;
        this.idPhoto = idPhoto ;
        this.sellPrice = sellPrice ;
        this.category = category ;
        this.exp = exp ;
        this.idPhotoEarly  =idPhotoEarly ;
    }
    public int getIdPhoto() {
        return idPhoto;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPhoto() {
        return photo;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Item(){

    }
}