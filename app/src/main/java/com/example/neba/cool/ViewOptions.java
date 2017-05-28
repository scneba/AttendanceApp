package com.example.neba.cool;

public class ViewOptions {

    private String begin_dt,end_dt,coordinates,picture1,picture2,created_at;
    private Integer id,EmId;


    public ViewOptions(Integer Id,Integer Emid,String begin_dt,String end_dt,String coordinates,String picture1,String picture2,String created_at){
        this.begin_dt = begin_dt;
        this.end_dt = end_dt;
        this.coordinates=coordinates;
        this.picture1=picture1;
        this.picture2=picture2;
        this.created_at=created_at;
        this.id=Id;
        this.EmId=Emid;
    }

    public String getBegin_dt(){
        return this.begin_dt;
    }
    public String getEnd_dt(){
        return this.end_dt;
    }
    public String getCoordinates(){
        return this.coordinates;
    }
    public String getPicture1(){
        return this.picture1;
    }
    public String getPicture2(){
        return this.picture2;
    }
    public String getCreated_at(){
        return this.created_at;
    }

    public Integer getEmId(){
        return this.EmId;
    }
    public Integer getId(){
        return this.id;
    }


}
