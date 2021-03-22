package com.example.ardomino;

import android.graphics.PointF;

import java.util.ArrayList;

public class Circle {
    private PointF start;
    private PointF end;
    private PointF center;
    private PointF intersect;
    private float radius;

    public Circle(PointF st,PointF en,PointF cen,PointF inter,float rad){
        this.start=new PointF();
        this.end=new PointF();
        this.center= new PointF();
        this.intersect=new PointF();
        this.start.x=st.x;
        this.start.y=st.y;
        this.end.x=en.x;
        this.end.y=en.y;
        this.center.x=cen.x;
        this.center.y=cen.y;
        this.intersect.x=inter.x;
        this.intersect.y=inter.y;
        this.radius=rad;
    }
    public String PosOrNeg(PointF p){
        if((Math.pow(Line.Robot_Raduis, 2) - Math.pow(p.x - center.x, 2))<0||(Math.pow(Line.Robot_Raduis, 2) - Math.pow(p.x - center.x, 2))<0){
            return "equ";
        }
        float X_positiveY = (float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(p.x - center.x, 2)) + center.y;
        float X_negativeY = (float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(p.x - center.x, 2)) + center.y;
        if(X_positiveY+1>=X_negativeY && X_positiveY-1<=X_negativeY)
            return "equ";
        if(X_positiveY<=p.y+1&&X_positiveY>=p.y-1)
            return "pos";
        return "neg";
    }
    public void AddCirclePart(ArrayList<Line> lines){

        if(PosOrNeg(start)=="pos"&&PosOrNeg(end)=="pos"||PosOrNeg(start)=="pos"&&PosOrNeg(end)=="equ"||PosOrNeg(start)=="equ"&&PosOrNeg(end)=="pos"){

            if(this.start.x<this.end.x){
                float last=this.start.x;
                float jump=(this.end.x-this.start.x)/100;
                int count=1;
                for (float x = (float) (last+jump); x <= end.x||count<=100; x += jump) {
                    float Last_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
            }else {
                float last=this.start.x;
                float jump=(this.start.x-this.end.x)/100;
                int count=1;
                for (float x = (float) (last-jump); x >= end.x||count<=100; x -= jump) {
                    float Last_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
            }

        }
        if(PosOrNeg(start)=="neg"&&PosOrNeg(end)=="neg"||PosOrNeg(start)=="neg"&&PosOrNeg(end)=="equ"||PosOrNeg(start)=="equ"&&PosOrNeg(end)=="neg"){
            if(this.start.x<this.end.x){
                float last=this.start.x;
                float jump=(this.end.x-this.start.x)/100;
                int count=1;
                for (float x = (float) (last+jump); x <= end.x||count<=100; x += jump) {
                    float Last_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
            }else {
                float last=this.start.x;
                float jump=(this.start.x-this.end.x)/100;
                int count=1;
                for (float x = (float) (last-jump); x >= end.x||count<=100; x -= jump) {
                    float Last_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
            }
        }
        if(PosOrNeg(start)=="pos"&&PosOrNeg(end)=="neg"){
            float edge=0;
            if((new Line(intersect,new PointF(center.x+Line.Robot_Raduis,center.y))).GetDist()<(new Line(intersect,new PointF(center.x-Line.Robot_Raduis,center.y))).GetDist()){

                edge=(center.x+Line.Robot_Raduis);
                float last=this.start.x;
                float jump=(edge-last)/50;
                int count=1;
                for (float x = (float) (last+jump); x <= edge||count<=50; x += jump) {
                    float Last_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
                last=edge;
                jump=(edge-end.x)/50;
                count=1;
                for (float x = (float) (edge-jump); x >= end.x||count<=50; x -= jump) {
                    float Last_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }

            }
            if((new Line(intersect,new PointF(center.x+Line.Robot_Raduis,center.y))).GetDist()>(new Line(intersect,new PointF(center.x-Line.Robot_Raduis,center.y))).GetDist()){
                edge=(center.x-Line.Robot_Raduis);
                float last=this.start.x;
                float jump=(last-edge)/50;
                int count=1;
                for (float x = (float) (last-jump); x >= edge||count<=50; x -= jump) {
                    float Last_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
                last=edge;
                jump=(end.x-edge)/50;
                count=1;
                for (float x = (float) (edge+jump); x <= end.x||count<=50; x += jump) {
                    float Last_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }

            }

        }
        if(PosOrNeg(start)=="neg"&&PosOrNeg(end)=="pos"){
            float edge=0;
            if((new Line(intersect,new PointF(center.x+Line.Robot_Raduis,center.y))).GetDist()<(new Line(intersect,new PointF(center.x-Line.Robot_Raduis,center.y))).GetDist()){

                edge=(center.x+Line.Robot_Raduis);
                float last=this.start.x;
                float jump=(edge-last)/50;
                int count=1;
                for (float x = (float) (last+jump); x <= edge||count<=50; x += jump) {
                    float Last_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
                last=edge;
                jump=(edge-end.x)/50;
                count=1;
                for (float x = (float) (last-jump); x >= end.x||count<=50; x -= jump) {
                    float Last_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
            }
            if((new Line(intersect,new PointF(center.x+Line.Robot_Raduis,center.y))).GetDist()>(new Line(intersect,new PointF(center.x-Line.Robot_Raduis,center.y))).GetDist()){
                edge=(center.x-Line.Robot_Raduis);
                float last=this.start.x;
                float jump=(last-edge)/50;
                int count=1;
                for (float x = (float) (last-jump); x >= edge||count<=50; x -= jump) {
                    float Last_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) -Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
                last=edge;
                jump=(end.x-edge)/50;
                count=1;
                for (float x = (float) (last+jump); x <= end.x||count<=50; x += jump) {
                    float Last_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y;
                    float X_y=(float) Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y;
                    lines.add(lines.size(),new Line((new PointF(last,Last_y)),(new PointF(x,X_y))));
                    last=x;
                    count++;
                }
            }

        }
        if(PosOrNeg(start)=="equ"&&PosOrNeg(end)=="equ"){
            int PosOrNeg=0;
            if((new Line(intersect,new PointF(center.x,center.y+Line.Robot_Raduis))).GetDist()<(new Line(intersect,new PointF(center.x,center.y-Line.Robot_Raduis))).GetDist()){

                PosOrNeg=1;
            }
            if((new Line(intersect,new PointF(center.x,center.y+Line.Robot_Raduis))).GetDist()>(new Line(intersect,new PointF(center.x,center.y-Line.Robot_Raduis))).GetDist()){
                PosOrNeg=-1;
            }
            if(this.start.x<this.end.x) {
                float last = this.start.x;
                float jump = (end.x - last) / 100;
                int count=1;
                for (float x = (float) (last + jump); x <= end.x||count<=100; x += jump) {
                    float Last_y = (float) ((float) PosOrNeg * Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y);
                    float X_y = (float) ((float) PosOrNeg * Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y);
                    lines.add(lines.size(), new Line((new PointF(last, Last_y)), (new PointF(x, X_y))));
                    last = x;
                    count++;
                }
            }else {
                float last = this.start.x;
                float jump = (last-end.x ) / 100;
                int count=1;
                for (float x = (float) (last - jump); x >= end.x||count<=100; x -= jump) {
                    float Last_y = (float) ((float) PosOrNeg * Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(last - center.x, 2)) + center.y);
                    float X_y = (float) ((float) PosOrNeg * Math.sqrt(Math.pow(Line.Robot_Raduis, 2) - Math.pow(x - center.x, 2)) + center.y);
                    lines.add(lines.size(), new Line((new PointF(last, Last_y)), (new PointF(x, X_y))));
                    last = x;
                    count++;
                }
            }
        }
    }


}
