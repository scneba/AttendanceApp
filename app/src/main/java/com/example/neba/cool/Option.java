package com.example.neba.cool;

public class Option {

	private String title,message,date;
    private Integer seen,id;

     
    public Option(Integer id,String title,String message,String date,Integer seen){
        this.message = message;
        this.title = title;
        this.date=date;
        this.seen=seen;
        this.id=id;
    }

    public String getTitle(){
        return this.title;
    }
    public String getMessage(){
        return this.message;
    }
     
    public String getDate(){
        return this.date;
    }
    public Integer getSeen(){
        return this.seen;
    }
    public Integer getId(){
        return this.id;
    }
 
 
}
