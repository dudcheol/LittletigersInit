package com.example.parkyoungcheol.littletigersinit.Model;


// 게터,세터 만드는 법 => Alt+Insert
public class DataSet {
    String title, link, category, description, telephone, address, roadAddress;
    int mapx, mapy;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAddress() {
        return address;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public int getMapx() {
        return mapx;
    }

    public int getMapy() {
        return mapy;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public void setMapx(int mapx) {
        this.mapx = mapx;
    }

    public void setMapy(int mapy) {
        this.mapy = mapy;
    }
}
