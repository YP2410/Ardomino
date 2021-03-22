package com.example.ardomino;

import android.graphics.PointF;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Line {
    public static float Ratio= (float)(400.0/1440);
    public static  float Robot_Raduis = (float) 14.5/Ratio;
    public static  float Wheel_Radius = (float) 4.0;
    public static  float Step_Len = (float) ((float) (2*Math.PI*Wheel_Radius)/200.0)/Ratio;
    public static  float Domino_Len = (float) 3.1/Ratio;
    public static  int Domino_Number = (int) 30.0;
    public static float Danger_Space= (float) 2.5 / Ratio;

    private PointF p1;//IntersectPoint
    private PointF p2;
    public PointF p1_Restore;
    public PointF p2_Restore;

    public Line(PointF p1_,PointF p2_){
        this.p1=new PointF(p1_.x,p1_.y);
        this.p2=new PointF(p2_.x,p2_.y);
    }
    public Line(Line l1){
        this.p1=new PointF(l1.p1.x,l1.p1.y);
        this.p2=new PointF(l1.p2.x,l1.p2.y);
        if(l1.p1_Restore!=null) {
            this.p1_Restore = new PointF(l1.p1_Restore.x, l1.p1_Restore.y);
        }
        if(l1.p2_Restore!=null) {
            this.p2_Restore = new PointF(l1.p2_Restore.x, l1.p2_Restore.y);
        }
    }
    public PointF GetP1(){
        return this.p1;
    }
    public  PointF GetP2(){
        return this.p2;
    }
    public void SetP1(PointF p,boolean backup){
        if(backup) {
            this.p1_Restore = new PointF(this.p1.x, this.p1.y);
        }
        this.p1.x=p.x;
        this.p1.y=p.y;
    }
    public void SetP2(PointF p,boolean backup){
        if(backup) {
            this.p2_Restore = new PointF(this.p2.x, this.p2.y);
        }
        this.p2.x=p.x;
        this.p2.y=p.y;
    }
    public void RestoreP1(){
        this.p1.x=this.p1_Restore.x;
        this.p1.y=this.p1_Restore.y;
    }
    public void RestoreP2(){
        this.p2.x=this.p2_Restore.x;
        this.p2.y=this.p2_Restore.y;
    }
    public float Get_M(){
        return (p2.y-p1.y)/(p2.x-p1.x);
    }
    public float Get_N(){
        return p2.y-p2.x*Get_M();
    }
    public static float[] PolySolve(double a,double b,double c){
        float [] returned=new float[2];
        /*float check1= (float) Math.pow(b,2);
        long dddd= (long) Math.pow(b,2);
        long check2= (long) (4*a*c);
        long saS=dddd-check2;
        double cc=4*a*c;
        float check3= (float) (Math.pow(b,2)-4*a*c);*/
        if(Math.pow(b,2)-4*a*c<0){
            returned[0]=-1;
            returned[1]=-1;
            return returned;
        }
        returned[0]= (float) ((float) (-b+Math.sqrt(Math.pow(b,2)-4*a*c))/(2*a));
        returned[1]= (float) ((float) (-b-Math.sqrt(Math.pow(b,2)-4*a*c))/(2*a));
        return returned;
    }
    public  boolean Region(PointF check){
        return (check.x<=Math.max(p1.x,p2.x))&&(check.x>=Math.min(p1.x,p2.x))&&(check.y<=Math.max(p1.y,p2.y))&&(check.y>=Math.min(p1.y,p2.y));
    }
    public boolean LineAndRegion(PointF check){
        if(p1.x==p2.x){
            return check.x==p1.x&&(check.y<=Math.max(p1.y,p2.y))&&(check.y>=Math.min(p1.y,p2.y));
        }
        if(p1.y==p2.y){
            return check.y==p1.y&&(check.x<=Math.max(p1.x,p2.x))&&(check.x>=Math.min(p1.x,p2.x));
        }
        return (check.x*Get_M()+Get_N()==check.y)&&(check.x<=Math.max(p1.x,p2.x))&&(check.x>=Math.min(p1.x,p2.x))&&(check.y<=Math.max(p1.y,p2.y))&&(check.y>=Math.min(p1.y,p2.y));
    }
    public PointF GetPointOn_d(float d,boolean p1_Interscet){

        float[] points=null;
        if(p1_Interscet) {
            if(p1.x==p2.x){
                if(LineAndRegion(new PointF( p1.x, p1.y+d)))
                    return new PointF( p1.x, p1.y+d);
                if(LineAndRegion(new PointF( p1.x, p1.y-d)))
                    return new PointF( p1.x, p1.y-d);
                return new PointF(-1,-1);
            }
            if(p1.y==p2.y){
                if(LineAndRegion(new PointF( p1.x+d, p1.y)))
                    return new PointF( p1.x+d, p1.y);
                if(LineAndRegion(new PointF( p1.x-d, p1.y)))
                    return new PointF( p1.x-d, p1.y);
                return new PointF(-1,-1);
            }
            float check1=Get_M();
            float check2=Get_N();
            double coef1=(double) (Math.pow(Get_M(), 2) + 1);
            double coef2_1=(double) -(2 * this.p1.x );
            double coef2_2=-2 * Get_M() * (this.p1.y - Get_N());
            double coef2=coef2_1+coef2_2;
            double coef3_1=(double) (Math.pow(this.p1.x, 2) );
            double coef3_2= Math.pow(this.p1.y - Get_N(), 2);
            double coef3_3=- Math.pow(d, 2);
            double coef333=(coef3_1+coef3_2+coef3_3);

            double update1=(coef1-((coef1%1))+ ((((coef1%1)*10000)-(((coef1%1)*10000)%1))/10000));
            double update2=(coef2-((coef2%1))+ ((((coef2%1)*10000)-(((coef2%1)*10000)%1))/10000));;
            double update3=(coef333-((coef333%1))+ ((((coef333%1)*10000)-(((coef333%1)*10000)%1))/10000));
            points = PolySolve((double) (Math.pow(Get_M(), 2) + 1),(double) -(2 * this.p1.x + 2 * Get_M() * (this.p1.y - Get_N())), (double) (Math.pow(this.p1.x, 2) + Math.pow(this.p1.y - Get_N(), 2) - Math.pow(d, 2)));
            if(points[0]==-1&&points[1]==-1){
                points=PolySolve(update1,update2, update3);
            }
        }else {
            if(p1.x==p2.x){
                if(LineAndRegion(new PointF( p2.x, p2.y+d)))
                    return new PointF( p2.x, p2.y+d);
                if(LineAndRegion(new PointF( p2.x, p2.y-d)))
                    return new PointF( p2.x, p2.y-d);
                return new PointF(-1,-1);
            }
            if(p1.y==p2.y){
                if(LineAndRegion(new PointF( p2.x+d, p2.y)))
                    return new PointF( p2.x+d, p2.y);
                if(LineAndRegion(new PointF( p2.x-d, p2.y)))
                    return new PointF( p2.x-d, p2.y);
                return new PointF(-1,-1);
            }
            float check1=Get_M();
            float check2=Get_N();
            long zaz= (long) Math.pow(Get_M(),2);
            double coef1=(double) (Math.pow(Get_M(), 2) + 1);
            int ddddd=((int)(coef1*10000));
            double ddd=ddddd/10000.0;
            double coef2_1=(double) -(2 * this.p2.x );
            double coef2_2=-2 * Get_M() * (this.p2.y - Get_N());
            double coef2=coef2_1+coef2_2;
            double coef3_1=(double) (Math.pow(this.p2.x, 2) );
            double coef3_2= Math.pow(this.p2.y - Get_N(), 2);
            double coef3_3=- Math.pow(d, 2);
            double coef333=(coef3_1+coef3_2+coef3_3);

            double update1=(coef1-((coef1%1))+ ((((coef1%1)*10000)-(((coef1%1)*10000)%1))/10000));
            double update2=(coef2-((coef2%1))+ ((((coef2%1)*10000)-(((coef2%1)*10000)%1))/10000));;
            double update3=(coef333-((coef333%1))+ ((((coef333%1)*10000)-(((coef333%1)*10000)%1))/10000));
            points = PolySolve((double) (Math.pow(Get_M(), 2) + 1),(double) -(2 * this.p2.x + 2 * Get_M() * (this.p2.y - Get_N())), (double) (Math.pow(this.p2.x, 2) + Math.pow(this.p2.y - Get_N(), 2) - Math.pow(d, 2)));
            if(points[0]==-1&&points[1]==-1){
                points=PolySolve(update1,update2, update3);
            }
        }
        if(points[0]>0&&LineAndRegion(new PointF(points[0],Get_M()*points[0]+Get_N()))){
            return new PointF(points[0],Get_M()*points[0]+Get_N());
        }
        return new PointF(points[1],Get_M()*points[1]+Get_N());
    }
    public String GetStatus(){
        if(p1.x==p2.x)
            return "Ver";
        if(p1.y==p2.y)
            return "Hor";
        return "Another";
    }

    private boolean NotOneOfThem(PointF p_check){
        if(!p_check.equals(this.p1)&&!p_check.equals(this.p2)&& (new Line(p1,p_check)).GetDist()>0.02&&(new Line(p2,p_check)).GetDist()>0.02)
            return true;
        return false;
    }
    public boolean CheckIntersect(Line anotherOne) {
        if (this.GetStatus() == "Ver" && anotherOne.GetStatus() == "Ver") {
            boolean b1= (  anotherOne.NotOneOfThem(this.p1)&&(this.p1.y>=Math.min(anotherOne.p1.y,anotherOne.p2.y)-0.2) &&(this.p1.y<=Math.max(anotherOne.p1.y,anotherOne.p2.y)+0.2 ) );
            boolean b2= (  anotherOne.NotOneOfThem(this.p2)&&(this.p2.y>=Math.min(anotherOne.p1.y,anotherOne.p2.y)-0.2) &&(this.p2.y<=Math.max(anotherOne.p1.y,anotherOne.p2.y)+0.2 ));
            boolean b3=(this.NotOneOfThem(anotherOne.p1) && (anotherOne.p1.y>=Math.min(this.p1.y,this.p2.y)-0.2) &&(anotherOne.p1.y<=Math.max(this.p1.y,this.p2.y)+0.2 ));
            boolean b4=(   this.NotOneOfThem(anotherOne.p2)&& (anotherOne.p2.y>=Math.min(this.p1.y,this.p2.y)-0.2) &&(anotherOne.p2.y<=Math.max(this.p1.y,this.p2.y)+0.2 )  ) ;

            return (anotherOne.GetP1().x == this.GetP1().x)&& (b1||b2||b3||b4);
        }
        if (this.GetStatus() == "Ver" && anotherOne.GetStatus() == "Hor") {
            PointF center=new PointF(this.p1.x,anotherOne.p2.y);
            return this.NotOneOfThem(center)&& center.x <= Math.max(anotherOne.p1.x, anotherOne.p2.x)+0.2 && center.x >= Math.min(anotherOne.p1.x, anotherOne.p2.x)-0.2
                    && center.y <= Math.max(this.p1.y, this.p2.y)+0.2 && center.y >= Math.min(this.p1.y, this.p2.y)-0.2;
        }
        if (this.GetStatus() == "Hor" && anotherOne.GetStatus() == "Ver") {
            PointF center=new PointF(anotherOne.p1.x,this.p2.y);
            return this.NotOneOfThem(center)&&center.x <= Math.max(this.p1.x, this.p2.x)+0.2 && center.x >= Math.min(this.p1.x, this.p2.x)-0.2
                    && center.y <= Math.max(anotherOne.p1.y, anotherOne.p2.y)+0.2 && center.y >= Math.min(anotherOne.p1.y, anotherOne.p2.y)-0.2;
        }
        if (this.GetStatus() == "Hor" && anotherOne.GetStatus() == "Hor") {
            boolean b1= (  anotherOne.NotOneOfThem(this.p1)&&(this.p1.x>=Math.min(anotherOne.p1.x,anotherOne.p2.x)-0.2) &&(this.p1.x<=Math.max(anotherOne.p1.x,anotherOne.p2.x)+0.2 ) );
            boolean b2= (  anotherOne.NotOneOfThem(this.p2)&&(this.p2.x>=Math.min(anotherOne.p1.x,anotherOne.p2.x)-0.2) &&(this.p2.x<=Math.max(anotherOne.p1.x,anotherOne.p2.x)+0.2 ));
            boolean b3=(this.NotOneOfThem(anotherOne.p1) && (anotherOne.p1.x>=Math.min(this.p1.x,this.p2.x)-0.2) &&(anotherOne.p1.x<=Math.max(this.p1.x,this.p2.x)+0.2 ));
            boolean b4=(   this.NotOneOfThem(anotherOne.p2)&& (anotherOne.p2.x>=Math.min(this.p1.x,this.p2.x)-0.2) &&(anotherOne.p2.x<=Math.max(this.p1.x,this.p2.x)+0.2 )  ) ;

            return (anotherOne.GetP1().y == this.GetP1().y)&& (b1||b2||b3||b4);
        }

        float intersectX=0;
        float intersectY=0;
        if(this.GetStatus() == "Ver"){
            intersectX=this.p1.x;
            intersectY=anotherOne.Get_M()*intersectX+anotherOne.Get_N();
        }else {
            if (this.GetStatus() == "Hor") {
                intersectY = this.p1.y;
                intersectX = (intersectY - anotherOne.Get_N()) / anotherOne.Get_M();
            } else {
                if (anotherOne.GetStatus() == "Ver") {
                    intersectX = anotherOne.p1.x;
                    intersectY = this.Get_M() * intersectX + this.Get_N();
                } else {
                    if (anotherOne.GetStatus() == "Hor") {
                        intersectY = anotherOne.p1.y;
                        intersectX = (intersectY - this.Get_N()) / this.Get_M();
                    } else {
                        //ax+b=cx+d
                        //(a-c)x=d-b
                        float m1_check = this.Get_M();
                        float m2_check = anotherOne.Get_M();
                        float n1_check = this.Get_N();
                        float n2_check = anotherOne.Get_N();
                        intersectX = (anotherOne.Get_N() - this.Get_N()) / (this.Get_M() - anotherOne.Get_M());
                        intersectY = this.Get_M() * intersectX + this.Get_N();
                    }
                }
            }
        }


        boolean b1=intersectX<=Math.max(this.p1.x,this.p2.x)+0.2;
        boolean b2=intersectX>=Math.min(this.p1.x,this.p2.x)-0.2;
        boolean b3=intersectY<=Math.max(this.p1.y,this.p2.y)+0.2;
        boolean b4=intersectY>=Math.min(this.p1.y,this.p2.y)-0.2;
        boolean b5=intersectX<=Math.max(anotherOne.p1.x,anotherOne.p2.x)+0.2;
        boolean b6=intersectX>=Math.min(anotherOne.p1.x,anotherOne.p2.x)-0.2;
        boolean b7=intersectY<=Math.max(anotherOne.p1.y,anotherOne.p2.y)+0.2;
        boolean b8=intersectY>=Math.min(anotherOne.p1.y,anotherOne.p2.y)-0.2;

        return NotOneOfThem(new PointF(intersectX,intersectY))&&b1&&b2&&b3&&b4&&b5&&b6&&b7&&b8;
    }
    public float GetDist(){
        return (float) Math.sqrt(Math.pow(this.p1.x-this.p2.x,2)+Math.pow(this.p1.y-this.p2.y,2));
    }
    public PointF[] Get_This_Another_StartCirclePoints(Line l2){
        //p1-->p2p1'-->p2'
        //this=p1-->p2
        Line connected=new Line(this.p1,l2.p2);
        float a2= (float) Math.pow(this.GetDist(),2);
        float b2= (float) Math.pow(l2.GetDist(),2);
        float c2= (float) Math.pow(connected.GetDist(),2);
        float degree= (float) Math.toDegrees(Math.acos(   (a2+b2-c2)/(2*Math.sqrt(a2)*Math.sqrt(b2)) ));
        double aa=Math.tan(Math.toRadians(degree/2));
        float d= (float) (Robot_Raduis/Math.tan(Math.toRadians(degree/2)));
        float ch1=this.GetDist();
        float ch2=l2.GetDist();
        if(d<=this.GetDist()&&d<=l2.GetDist()){//
            int dss=1;
        }
        PointF thisPoint_Radius=this.GetPointOn_d(d,false);
        PointF l2Point_Radius=l2.GetPointOn_d(d,true);
        PointF thisPoint_Radius4=this.GetPointOn_d(d,false);
        PointF l2Point_Radius4=l2.GetPointOn_d(d,true);
        PointF[] arr=new PointF[2];
        arr[0]=thisPoint_Radius;
        arr[1]=l2Point_Radius;
        return arr;
    }
    public float GetDegrees(Line l2){
        //p1-->p2p1'-->p2'
        //this=p1-->p2
        Line connected=new Line(this.p1,l2.p2);
        float a2= (float) Math.pow(this.GetDist(),2);
        float b2= (float) Math.pow(l2.GetDist(),2);
        double zz=connected.GetDist();
        double z1=connected.GetDist();
        float c2= (float) Math.pow(connected.GetDist(),2);
        if((this.GetDist()+l2.GetDist())<=connected.GetDist()+0.01 && (this.GetDist()+l2.GetDist())>=connected.GetDist()-0.01)
            return 180;
        float degree= (float) Math.toDegrees(Math.acos(   (a2+b2-c2)/(2*Math.sqrt(a2)*Math.sqrt(b2)) ));
        return degree;
    }
    public PointF GetMidCircle(Line l2){
        //p1-->p2p1'-->p2'
        //this=p1-->p2
        Line connected=new Line(this.p1,l2.p2);
        float a2= (float) Math.pow(this.GetDist(),2);
        float b2= (float) Math.pow(l2.GetDist(),2);
        float c2= (float) Math.pow(connected.GetDist(),2);
        float degree=0;
        if((this.GetDist()+l2.GetDist())<=connected.GetDist()+0.01 && (this.GetDist()+l2.GetDist())>=connected.GetDist()-0.01){
            degree=180;
        }else {
            degree= (float) Math.toDegrees(Math.acos(   (a2+b2-c2)/(2*Math.sqrt(a2)*Math.sqrt(b2)) ));
        }
        double aa=Math.tan(Math.toRadians(degree/2));
        float d= (float) (Robot_Raduis/Math.tan(Math.toRadians(degree/2)));

        if(d>=this.GetDist()||d>=l2.GetDist()){//
            return new PointF(-1,-1);
        }
        PointF thisPoint_Radius=this.GetPointOn_d(d,false);
        if(thisPoint_Radius.x==-1){
            int dddd=1;
            this.GetPointOn_d(d,false);
        }
        PointF l2Point_Radius=l2.GetPointOn_d(d,true);
        if(l2Point_Radius.x==-1){
            int dddd=1;
            l2.GetPointOn_d(d,false);
        }
        float cc1=(new Line(thisPoint_Radius,this.p2)).GetDist();
        float cc2=(new Line(l2Point_Radius,this.p2)).GetDist();

        PointF center=new PointF(0,0);
        if (this.GetStatus() == "Ver") {
            center.y=thisPoint_Radius.y;
            if (l2.GetStatus() == "Hor") {
                center.x=l2Point_Radius.x;
                return center;
            }
            if (l2.GetStatus() == "Ver") {
                return new PointF(-1,-1);
            }
            center.x=(center.y- l2Point_Radius.y)/(-1/l2.Get_M())+l2Point_Radius.x;
            float check1=(new Line(center,thisPoint_Radius)).GetDist();
            float check2=(new Line(center,l2Point_Radius)).GetDist();
            return center;
        }
        if (this.GetStatus() == "Hor") {
            center.x=thisPoint_Radius.x;
            if (l2.GetStatus() == "Ver") {
                center.y=l2Point_Radius.y;
                return center;
            }
            if (l2.GetStatus() == "Hor") {
                return new PointF(-1,-1);
            }
            center.y=(-1/l2.Get_M())*center.x+ (l2Point_Radius.y- (-1/l2.Get_M())*l2Point_Radius.x );
            float check1=(new Line(center,thisPoint_Radius)).GetDist();
            float check2=(new Line(center,l2Point_Radius)).GetDist();
            return center;
        }
        if (l2.GetStatus() == "Ver") {
            center.y=l2Point_Radius.y;
            if (this.GetStatus() == "Hor") {
                center.x=thisPoint_Radius.x;
                return center;
            }
            if (this.GetStatus() == "Ver") {
                return new PointF(-1,-1);
            }
            center.x=(center.y- thisPoint_Radius.y)/(-1/this.Get_M())+thisPoint_Radius.x;
            float check1=(new Line(center,thisPoint_Radius)).GetDist();
            float check2=(new Line(center,l2Point_Radius)).GetDist();
            return center;
        }
        if (l2.GetStatus() == "Hor") {
            center.x=l2Point_Radius.x;
            if (this.GetStatus() == "Ver") {
                center.y=thisPoint_Radius.y;
                return center;
            }
            if (this.GetStatus() == "Hor") {
                return new PointF(-1,-1);
            }
            center.y=(-1/this.Get_M())*center.x+(thisPoint_Radius.x/this.Get_M())+thisPoint_Radius.y;
            float check1=(new Line(center,thisPoint_Radius)).GetDist();
            float check2=(new Line(center,l2Point_Radius)).GetDist();
            if((new Line(center,thisPoint_Radius)).GetDist()>Robot_Raduis+0.2||(new Line(center,thisPoint_Radius)).GetDist()<Robot_Raduis-0.2||(new Line(center,l2Point_Radius)).GetDist()>Robot_Raduis+0.2||(new Line(center,l2Point_Radius)).GetDist()<Robot_Raduis-0.2){
                return new PointF(-1,-1);
            }
            return center;
        }
        float R1_m=-1/this.Get_M();
        float R2_m=-1/l2.Get_M();
        float R1_n=(thisPoint_Radius.x/this.Get_M())+thisPoint_Radius.y;
        float R2_n=(l2Point_Radius.x/l2.Get_M())+l2Point_Radius.y;
        center.x=(R1_n-R2_n)/(R2_m-R1_m);
        center.y=R1_m*center.x+R1_n;
        return center;
    }
    public Line[] ParallelLines(){
        Line[] returned=new Line[2];
        if(this.GetStatus()=="Ver"){
            returned[0]=new Line((new PointF(this.p1.x+Robot_Raduis,this.p1.y)),(new PointF(this.p2.x+Robot_Raduis,this.p2.y)));
            returned[1]=new Line((new PointF(this.p1.x-Robot_Raduis,this.p1.y)),(new PointF(this.p2.x-Robot_Raduis,this.p2.y)));
            return returned;
        }
        if(this.GetStatus()=="Hor"){
            returned[0]=new Line((new PointF(this.p1.x,this.p1.y+Robot_Raduis)),(new PointF(this.p2.x,this.p2.y+Robot_Raduis)));
            returned[1]=new Line((new PointF(this.p1.x,this.p1.y-Robot_Raduis)),(new PointF(this.p2.x,this.p2.y-Robot_Raduis)));
            return returned;
        }
        float m=this.Get_M();
        float n=this.Get_N();
        float C1= (float) (-Robot_Raduis*Math.sqrt(Math.pow(this.Get_M(),2)+1)+this.Get_N());
        float C2= (float) (Robot_Raduis*Math.sqrt(Math.pow(this.Get_M(),2)+1)+this.Get_N());
        float X1=(((this.p1.x)/this.Get_M())+this.p1.y-C1)/(this.Get_M()+ (1/this.Get_M()));
        float X2=(((this.p2.x)/this.Get_M())+this.p2.y-C1)/(this.Get_M()+ (1/this.Get_M()));
        float Y1=(-1/this.Get_M())*X1+(this.p1.x/this.Get_M())+this.p1.y;
        float Y2=(-1/this.Get_M())*X2+(this.p2.x/this.Get_M())+this.p2.y;
        /*float X1=this.p1.x;
        float X2=this.p2.x;
        float Y1=this.Get_M()*X1+C1;
        float Y2=this.Get_M()*X2+C1;*/
        returned[0]=new Line(new PointF(X1,Y1),new PointF(X2,Y2));
        X1=(((this.p1.x)/this.Get_M())+this.p1.y-C2)/(this.Get_M()+ (1/this.Get_M()));
        X2=(((this.p2.x)/this.Get_M())+this.p2.y-C2)/(this.Get_M()+ (1/this.Get_M()));
        Y1=(-1/this.Get_M())*X1+(this.p1.x/this.Get_M())+this.p1.y;
        Y2=(-1/this.Get_M())*X2+(this.p2.x/this.Get_M())+this.p2.y;
        /* X1=this.p1.x;
         X2=this.p2.x;
         Y1=this.Get_M()*X1+C2;
         Y2=this.Get_M()*X2+C2;*/
        returned[1]=new Line(new PointF(X1,Y1),new PointF(X2,Y2));
        return returned;
    }
    public static PointF HipotaticIntersect(Line l1,Line l2){
        PointF inter=new PointF();
        if (l1.GetStatus() == "Ver" && l2.GetStatus() == "Ver") {
            if(l1.p1.x!=l2.p1.x)
                return new PointF(-1,-1);
            inter.x=l1.p1.x;
            inter.y=l1.p1.y;
            return inter;
        }
        if (l1.GetStatus() == "Ver" && l2.GetStatus() == "Hor") {
            inter.x=l1.p1.x;
            inter.y=l2.p1.y;
            return inter;
        }
        if (l1.GetStatus() == "Hor" && l2.GetStatus() == "Ver") {
            inter.x=l2.p1.x;
            inter.y=l1.p1.y;
            return inter;
        }
        if (l1.GetStatus() == "Hor" && l2.GetStatus() == "Hor") {
            if(l1.p1.y!=l2.p1.y)
                return new PointF(-1,-1);
            inter.x=l1.p1.x;
            inter.y=l1.p1.y;
            return inter;
        }


        if(l1.GetStatus() == "Ver"){
            inter.x=l1.p1.x;
            inter.y=l2.Get_M()*inter.x+l2.Get_N();
        }else {
            if (l1.GetStatus() == "Hor") {
                inter.y = l1.p1.y;
                inter.x = (inter.y - l2.Get_N()) / l2.Get_M();
            } else {
                if (l2.GetStatus() == "Ver") {
                    inter.x = l2.p1.x;
                    inter.y = l1.Get_M() * inter.x + l1.Get_N();
                } else {
                    if (l2.GetStatus() == "Hor") {
                        inter.y = l2.p1.y;
                        inter.x = (inter.y - l1.Get_N()) / l1.Get_M();
                    } else {
                        //ax+b=cx+d
                        //(a-c)x=d-b

                        inter.x = (l2.Get_N() - l1.Get_N()) / (l1.Get_M() - l2.Get_M());
                        inter.y = l1.Get_M() * inter.x + l1.Get_N();
                    }
                }
            }
        }

        return inter;

    }
    /*public static int WhatIsConnected(ArrayList<Line> Par1,ArrayList<Line> Par2,Line[] linesAfter){
        if(Par1.get(Par1.size()-1).CheckIntersect(linesAfter[0])){
            return 0;//00
        }
        if(Par1.get(Par1.size()-1).CheckIntersect(linesAfter[1])){
            return 1;//01
        }
        if(Par2.get(Par2.size()-1).CheckIntersect(linesAfter[0])){
            return 10;//10
        }
        if(Par2.get(Par2.size()-1).CheckIntersect(linesAfter[1])){
            return 11;//11
        }
        return -1;
    }*/
    public static int WhatIsConnected(Line[] linesBefore, Line[] linesAfter){
        if(!linesBefore[0].NotOneOfThem(linesAfter[0].p1) ||linesBefore[0].CheckIntersect(linesAfter[0])){
            return 0;//00
        }
        if(!linesBefore[0].NotOneOfThem(linesAfter[1].p1) ||linesBefore[0].CheckIntersect(linesAfter[1])){
            return 1;//01
        }
        if(!linesBefore[1].NotOneOfThem(linesAfter[0].p1) ||linesBefore[1].CheckIntersect(linesAfter[0])){
            return 10;//10
        }
        if(!linesBefore[1].NotOneOfThem(linesAfter[1].p1) ||linesBefore[1].CheckIntersect(linesAfter[1])){
            return 11;//11
        }
        return -1;
    }
    public static boolean AproxNearBy(double base,double check,double r){
        return (check<=base+r)&&(check>=base-r);
    }
    /*public static double BestCloseAngle(double angle){
        double min_dist=Math.abs(MainActivity.GoodAngles.get(0)-angle);
        double min_ang=MainActivity.GoodAngles.get(0);
        for(int i=1;i<MainActivity.GoodAngles.size();i++){
            if(Math.abs(MainActivity.GoodAngles.get(i)-angle)<min_dist){
                min_dist=Math.abs(MainActivity.GoodAngles.get(i)-angle);
                min_ang=MainActivity.GoodAngles.get(i);
            }
        }
        return min_ang;
    }*/
    public PointF ModifyLine(Line prevLine){
        /*if(prevLine!=null&&!AproxNearBy(prevLine.GetDegrees(this),90,0.1)) {

            if(this.GetStatus()=="Ver"||prevLine.GetStatus()=="Ver"){
                Line NewPrev=new Line(new PointF(prevLine.GetP1().y,prevLine.GetP1().x),new PointF(prevLine.GetP2().y,prevLine.GetP2().x));
                Line NewThis=new Line(new PointF(this.GetP1().y,this.GetP1().x),new PointF(this.GetP2().y,this.GetP2().x));
                PointF Returned=NewThis.ModifyLine(NewPrev);
                return new PointF(Returned.y,Returned.x);
            }

            double degTan = Math.toDegrees(Math.atan((this.Get_M() - prevLine.Get_M()) / (1 + this.Get_M() * prevLine.Get_M())));
            double RealDeg = prevLine.GetDegrees(this);
            double given=BestCloseAngle(RealDeg);
            if(AproxNearBy(180-RealDeg,degTan,5)){
                given=180-given;
            }
            if(AproxNearBy(-RealDeg,degTan,5)){
                given=-given;

            }
            given=Math.toRadians(given);
            float Y2= (float) ((((Math.tan(given)+prevLine.Get_M())/(1-(Math.tan(given)*prevLine.Get_M())))*(this.GetP2().x-this.GetP1().x))+this.GetP1().y);
            //this.GetP2().y=Y2;
        }*/

        int StepsToCut=(int)(this.GetDist()/Step_Len);
        PointF temp=GetPointOn_d((float) StepsToCut*Step_Len,true);
        double LenToCut2=this.GetDist()/Step_Len;
        return temp;
    }
    public static String LeftOrRightCircle(Line currentLine,PointF center){
        if(currentLine.GetP1().x==currentLine.GetP2().x){
            if(currentLine.GetP1().y<currentLine.GetP2().y){
                if(center.x>currentLine.GetP2().x){
                    return "Right";
                }else {
                    return "Left";
                }
            }else {
                if(center.x<currentLine.GetP2().x){
                    return "Right";
                }else {
                    return "Left";
                }
            }
        }else {
            if (currentLine.Get_M() > 0) {
                if (currentLine.GetP1().x < currentLine.GetP2().x) {
                    if (center.x > currentLine.GetP2().x) {
                        return "Right";
                    } else {
                        return "Left";
                    }
                } else {
                    if (center.x < currentLine.GetP2().x) {
                        return "Right";
                    } else {
                        return "Left";
                    }
                }
            } else {
                if (currentLine.Get_M() < 0) {
                    if (currentLine.GetP1().x < currentLine.GetP2().x) {
                        if (center.x < currentLine.GetP2().x) {
                            return "Right";
                        } else {
                            return "Left";
                        }
                    } else {
                        if (center.x > currentLine.GetP2().x) {
                            return "Right";
                        } else {
                            return "Left";
                        }
                    }
                } else {
                    if (currentLine.GetP1().x < currentLine.GetP2().x) {
                        if (center.y < currentLine.GetP2().y) {
                            return "Right";
                        } else {
                            return "Left";
                        }
                    } else {
                        if (center.y > currentLine.GetP2().y) {
                            return "Right";
                        } else {
                            return "Left";
                        }
                    }
                }
            }
        }
    }
    /*public static ArrayList<int[]> MoveMotorsWisely(ArrayList<Line> lines){
        ArrayList<int[]> Motors=new ArrayList<int[]>();
        int StepPerPut= (int) (Domino_Len/Step_Len);
        int current=0;
        int Domino_Session=Domino_Number;
        while (current<lines.size()){
            /////////////////line
            int lineSteps= (int) (lines.get(current).GetDist()/Step_Len);
            for (int j=StepPerPut;j<=lineSteps;j+=StepPerPut){
                int [] LeftRight=new int[2];
                LeftRight[0]=StepPerPut;
                LeftRight[1]=StepPerPut;
                Motors.add(Motors.size(),LeftRight);
            }
            if(lineSteps%StepPerPut!=0) {
                int[] LeftRight = new int[2];
                LeftRight[0] = lineSteps % StepPerPut;
                LeftRight[1] = lineSteps % StepPerPut;
                Motors.add(Motors.size(), LeftRight);
            }
            ////////////////////
            if(current+1<lines.size()){
                Line bef=new Line(lines.get(current).p1,lines.get(current).p2_Restore);
                Line aft=new Line(lines.get(current+101).p1_Restore,lines.get(current+101).p2);
                PointF center=bef.GetMidCircle(aft);
                float deg=bef.GetDegrees(aft);

                double lenToDrive=2*Math.PI*Line.Robot_Raduis*((180-deg)/360);
                int StepsToDo= (int) (lenToDrive/Step_Len);
                StepPerPut= 2*StepPerPut;
                for (int j=StepPerPut;j<=StepsToDo;j+=StepPerPut){
                    int[] LeftRight=new int[2];
                    LeftRight[0]=(LeftOrRightCircle(bef,center)=="Left")?0:StepPerPut;
                    LeftRight[1]=(LeftOrRightCircle(bef,center)=="Right")?0:StepPerPut;
                    Motors.add(Motors.size(),LeftRight);
                }
                if(StepsToDo%StepPerPut!=0) {
                    int[] LeftRight = new int[2];
                    LeftRight[0] = (LeftOrRightCircle(bef, center) == "Left") ? 0 : StepsToDo % StepPerPut;
                    LeftRight[1] = (LeftOrRightCircle(bef, center) == "Right") ? 0 : StepsToDo % StepPerPut;
                    Motors.add(Motors.size(), LeftRight);
                }

            }
            current+=101;
        }
        return Motors;
    }*/
    public static  void MoveMotorsWisely(ArrayList<Line> lines){
        ArrayList<int[]> Motors=new ArrayList<int[]>();
        //Motors.add(Motors.size(),new ArrayList<int[]>());
        int StepPerPut= (int) (Domino_Len/Step_Len);
        int current=0;
        int Domino_Session=Domino_Number;
        while (current<lines.size()){
            /////////////////line
            int lineSteps= (int) (lines.get(current).GetDist()/Step_Len);
            for (int j=StepPerPut;j<=lineSteps;j+=StepPerPut){
                int [] LeftRight=new int[2];
                LeftRight[0]=StepPerPut;
                LeftRight[1]=StepPerPut;
                Motors.add(Motors.size(),LeftRight);
            }
            if(lineSteps%StepPerPut!=0) {
                if(lineSteps%StepPerPut<=(Line.Danger_Space/Line.Step_Len)){
                    Motors.get(Motors.size()-1)[0]+=lineSteps%StepPerPut;
                    Motors.get(Motors.size()-1)[1]+=lineSteps%StepPerPut;
                }else {
                    int[] LeftRight = new int[2];
                    LeftRight[0] = lineSteps % StepPerPut;
                    LeftRight[1] = lineSteps % StepPerPut;
                    Motors.add(Motors.size(),LeftRight);
                }
            }
            ////////////////////
            if(current+1<lines.size()){
                Line bef=new Line(lines.get(current).p1,lines.get(current).p2_Restore);
                Line aft=new Line(lines.get(current+101).p1_Restore,lines.get(current+101).p2);
                PointF center=bef.GetMidCircle(aft);
                float deg=bef.GetDegrees(aft);

                double lenToDrive=3*2*Math.PI*Line.Robot_Raduis*((180-deg)/360);
                int StepsToDo= (int) (lenToDrive/Step_Len);
                StepPerPut= 2*StepPerPut;
                for (int j=StepPerPut;j<=StepsToDo;j+=StepPerPut){
                    int[] LeftRight=new int[2];
                    LeftRight[0]=(LeftOrRightCircle(bef,center)=="Left")?0:StepPerPut;
                    LeftRight[1]=(LeftOrRightCircle(bef,center)=="Right")?0:StepPerPut;
                    Motors.add(Motors.size(),LeftRight);
                }
                if(StepsToDo%StepPerPut!=0) {
                    if(StepsToDo%StepPerPut<=(Line.Danger_Space/Line.Step_Len)){
                        Motors.get(Motors.size()-1)[0]+=StepsToDo%StepPerPut;
                        Motors.get(Motors.size()-1)[1]+=StepsToDo%StepPerPut;
                    }else {
                        int[] LeftRight = new int[2];
                        LeftRight[0] = (LeftOrRightCircle(bef, center) == "Left") ? 0 : StepsToDo % StepPerPut;
                        LeftRight[1] = (LeftOrRightCircle(bef, center) == "Right") ? 0 : StepsToDo % StepPerPut;
                        Motors.add(Motors.size(),LeftRight);                    }
                }
                StepPerPut=StepPerPut/2;
            }
            current+=101;
        }
        DrawingActivity.CurrentPrinting=Motors;
    }
}

