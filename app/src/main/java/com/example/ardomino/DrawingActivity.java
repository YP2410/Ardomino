package com.example.ardomino;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import android.util.Pair;
import android.graphics.PointF;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class DrawingActivity extends Activity {

    static final String TAG = "View Database";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef5;
    DatabaseReference myRef8;
    DatabaseReference myRef9;


    DatabaseReference databaseReference;

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef = database.getReference("arr");
    public static DatabaseReference myRef2 = database.getReference("Empty");
    public static DatabaseReference myRef10 = database.getReference("Selected");


    DrawingView dv ;
    private Paint mPaint;
    private Paint PlusPaint;
    private Paint sidePaint;
    private Paint clearPaint;
    private Paint checkPaint;
    private static final float Ver_Hor_Tolerance = (float) 20;
    public static ArrayList<Double> GoodAngles;
    public static ArrayList<int[]> CurrentPrinting;
    final PuttingDomino_Dialog puttingDia=new PuttingDomino_Dialog(DrawingActivity.this);
    public static int Prev_Pointer=0;
    public static int Current_Pointer=0;
    public static int Domino_Remain=0;
    public static boolean changed=false;

    public static ArrayList<Double> CheckGoodAngles(double Radius, double radius){
        double DistPerStep=(2*Math.PI*radius)/200;
        ArrayList<Double> firstEq=new ArrayList<Double>() ;
        ArrayList<Double> secondEq=new ArrayList<Double>() ;
        ArrayList<Double> res=new ArrayList<Double>() ;
        for(int steps=1;steps<=10000;steps++){

            double check1=180-(360*((steps*DistPerStep)/(2*Math.PI*Radius)));
            firstEq.add(check1);
        }
        for(int steps=1;steps<=10000;steps++){

            double check2=2*Math.toDegrees(Math.atan((Radius/(steps*DistPerStep))));
            secondEq.add(check2);
        }
        for(int i=0;i<firstEq.size();i++){
            for(int j=0;j<secondEq.size();j++){
                if((firstEq.get(i)<=secondEq.get(j)+0.5)&&(firstEq.get(i)>=secondEq.get(j)-0.5)){
                    res.add(firstEq.get(i));
                }
            }
        }
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Button btn=new Button(this);
        //GoodAngles=CheckGoodAngles(Line.Robot_Raduis,Line.Wheel_Radius);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef5 = mFirebaseDatabase.getReference("arr");
        myRef8 = mFirebaseDatabase.getReference("Empty");
        myRef9 = mFirebaseDatabase.getReference("Selected");
        myRef8.setValue(true);
        myRef9.setValue(false);
        databaseReference = FirebaseDatabase.getInstance().getReference("arr");



        CurrentPrinting=null;
        dv = new DrawingView(this);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        // mPaint.setPathEffect(new DashPathEffect(new float[]{5, 10, 15, 20}, 0));

        PlusPaint = new Paint();
        PlusPaint.setAntiAlias(true);
        PlusPaint.setDither(true);
        PlusPaint.setColor(Color.YELLOW);
        PlusPaint.setStyle(Paint.Style.STROKE);
        PlusPaint.setStrokeJoin(Paint.Join.ROUND);
        PlusPaint.setStrokeCap(Paint.Cap.ROUND);
        PlusPaint.setStrokeWidth(Ver_Hor_Tolerance*2);
        //mPaint.setPathEffect(new DashPathEffect(new float[]{5, 10, 15, 20}, 0));


        clearPaint = new Paint();
        clearPaint.setAntiAlias(true);
        clearPaint.setDither(true);
        clearPaint.setColor(Color.WHITE);
        clearPaint.setStyle(Paint.Style.STROKE);
        clearPaint.setStrokeJoin(Paint.Join.ROUND);
        clearPaint.setStrokeCap(Paint.Cap.ROUND);
        clearPaint.setStrokeWidth(13);

        checkPaint = new Paint();
        checkPaint.setAntiAlias(true);
        checkPaint.setDither(true);
        checkPaint.setColor(Color.BLACK);
        checkPaint.setStyle(Paint.Style.STROKE);
        checkPaint.setStrokeJoin(Paint.Join.ROUND);
        checkPaint.setStrokeCap(Paint.Cap.ROUND);
        checkPaint.setStrokeWidth(10);
        checkPaint.setPathEffect(new DashPathEffect(new float[]{5, 10, 15, 20}, 0));


        //d
        sidePaint = new Paint();
        sidePaint.setAntiAlias(true);
        sidePaint.setDither(true);
        sidePaint.setColor(Color.YELLOW);
        sidePaint.setStyle(Paint.Style.STROKE);
        sidePaint.setStrokeJoin(Paint.Join.ROUND);
        sidePaint.setStrokeCap(Paint.Cap.ROUND);
        sidePaint.setStrokeWidth(10);
        sidePaint.setPathEffect(new DashPathEffect(new float[]{5, 10, 15, 20}, 0));
    }

    public class DrawingView extends View {


        public int width;
        public  int height;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Path    Pra1Path;
        private Path    Pra2Path;
        private Path    clearPath;
        private Path    UpPath;
        private Path    DownPath;
        private Path    RightPath;
        private Path    LeftPath;
        private Paint   mBitmapPaint;
        private Path    sidePath;//d
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        private PointF p1 = new PointF(-1,-1);
        private PointF p2 = new PointF(-1,-1);
        private PointF p3 = new PointF(-1,-1);
        private Vector<Pair<PointF, PointF>> paths = new Vector<>(); // paths by their start and end points
        private Vector<PointF> points = new Vector<>(); // paths by their start and end points
        private RectF undoButton;
        private RectF redoButton;
        private RectF startButton;
        private RectF canvasFrame;

        private float mX, mY;
        private ArrayList<Line> lines=new ArrayList<Line>();
        private ArrayList<Line> Pra1lines=new ArrayList<Line>();
        private ArrayList<Line> Pra2lines=new ArrayList<Line>();
        private float sX, sY,sX1,sY1,sX2, sY2,sX3, sY3,sX4, sY4,sX5, sY5;
        private int count;
        private static final float TOUCH_TOLERANCE = 4;
        private static final double MIN_RADIUS = 0;
        private static final double SMALL_DISTANCE = 7;
        private int SCREEN_WIDTH;
        private int SCREEN_HEIGHT;


        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            Pra1Path=new Path();
            Pra2Path=new Path();
            clearPath=new Path();
            UpPath=new Path();
            DownPath=new Path();
            RightPath=new Path();
            LeftPath=new Path();

            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.RED);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

            sidePath = new Path();//d

            /*final Button undoButton = findViewById(R.id.undo);
            undoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    undo();
                }
            });

            final Button redoButton = findViewById(R.id.redo);
            redoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    redo();
                }
            });*/
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            //mCanvas = new Canvas(Bitmap.createBitmap(mBitmap, 0, (h-w)/2, w, w));
            mCanvas = new Canvas(mBitmap);

            canvasFrame = new RectF(0, (h-w)/2, w, (h-w)/2 + w);
            float buttonTop = canvasFrame.bottom + 30;
            float buttonBot = buttonTop + 150;
            redoButton = new RectF(0, buttonTop, w/3, buttonBot);
            startButton = new RectF(w/3, buttonTop, w*2/3, buttonBot);
            undoButton = new RectF(w*2/3, buttonTop, w, buttonBot);

            startScreen();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( Pra1Path,  checkPaint);
            canvas.drawPath( Pra2Path,  checkPaint);
            canvas.drawPath( clearPath,  clearPaint);
            canvas.drawPath( UpPath,  PlusPaint);
            canvas.drawPath( DownPath,  PlusPaint);
            canvas.drawPath( RightPath,  PlusPaint);
            canvas.drawPath( LeftPath,  PlusPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }
        private boolean CheckIfClose(PointF p1_, PointF p2_){
            if(p1_.x+Ver_Hor_Tolerance>=p2_.x&&p1_.x-Ver_Hor_Tolerance<=p2_.x){
                return true;
            }
            if(p1_.y+Ver_Hor_Tolerance>=p2_.y&&p1_.y-Ver_Hor_Tolerance<=p2_.y){
                return true;
            }
            return false;
        }


        private void touch_start(float x, float y) {
            sidePath.reset();
            UpPath.reset();
            DownPath.reset();
            RightPath.reset();
            LeftPath.reset();
            mPath.reset();
            Pra1Path.reset();
            Pra2Path.reset();
            mX = x;
            mY = y;
            if((redoButton.contains(x, y)||undoButton.contains(x, y)||startButton.contains(x, y))){
                return;
            }
            if(p1.equals(-1, -1)) {
                p1.set(x,y);
            }

            mPath.moveTo(p1.x, p1.y);
            sidePath.moveTo(p1.x, p1.y);
            UpPath.moveTo(p1.x,p1.y);
            DownPath.moveTo(p1.x,p1.y);
            LeftPath.moveTo(p1.x,p1.y);
            RightPath.moveTo(p1.x,p1.y);
            UpPath.lineTo(p1.x,p1.y-90);
            DownPath.lineTo(p1.x,p1.y+90);
            RightPath.lineTo(p1.x+90,p1.y);
            LeftPath.lineTo(p1.x-90,p1.y);
            count=0;
        }

        private void touch_move(float x, float y) {
            if(redoButton.contains(x, y)){
                return;
            }
            if(undoButton.contains(x, y)){
                return;
            }
            if(startButton.contains(x, y)){
                return;
            }
            if(!canvasFrame.contains(mX, mY)){
                return;
            }
            mPath.reset();
            sidePath.reset();
            mPath.moveTo(p1.x, p1.y);
            sidePath.moveTo(p1.x,p1.y);
            Pra1Path.reset();
            Pra2Path.reset();

            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                if(count==10)
                    // sidePath.moveTo(sX5,sY5);
                    if(count>=10) {
                        //   sidePath.quadTo(sX5, sY5, (sX5 + sX4) / 2, (sY5 + sY4) / 2);
                    }
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                sidePath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                count++;
                mX = x;
                mY = y;
                if(CheckIfClose(p1,new PointF(mX,mY))){
                    if(p1.x+Ver_Hor_Tolerance>=mX&&p1.x-Ver_Hor_Tolerance<=mX){
                        mX=p1.x;
                    }
                    if(p1.y+Ver_Hor_Tolerance>=mY&&p1.y-Ver_Hor_Tolerance<=mY){
                        mY=p1.y;
                    }
                }
                Line tempParralel=new Line(p1,new PointF(mX,mY));
                if(lines.size()>0&&tempParralel.GetDist()>5*Line.Robot_Raduis&&lines.get(lines.size()-1).GetDegrees(tempParralel)>30) {
                    if (Pra1lines.size() > 0) {
                        int d = 1;
                        d = 1;
                        d = 1;
                    }
                    float dddd=tempParralel.GetDist();
                    int size=Pra1lines.size();
                    ParallelFix(tempParralel);
                    Pra1Path.moveTo(Pra1lines.get(Pra1lines.size() - 1).GetP1().x, Pra1lines.get(Pra1lines.size() - 1).GetP1().y);
                    Pra1Path.lineTo(Pra1lines.get(Pra1lines.size() - 1).GetP2().x, Pra1lines.get(Pra1lines.size() - 1).GetP2().y);
                    Pra2Path.moveTo(Pra2lines.get(Pra2lines.size() - 1).GetP1().x, Pra2lines.get(Pra2lines.size() - 1).GetP1().y);
                    Pra2Path.lineTo(Pra2lines.get(Pra2lines.size() - 1).GetP2().x, Pra2lines.get(Pra2lines.size() - 1).GetP2().y);
                    if(Pra1lines.size()!=size) {
                        Pra1lines.remove(Pra1lines.size() - 1);
                        Pra2lines.remove(Pra2lines.size() - 1);

                        if (Pra1lines.size() > 0) {
                            Pra1lines.get(Pra1lines.size() - 1).RestoreP2();
                            Pra2lines.get(Pra2lines.size() - 1).RestoreP2();
                        }
                    }
                }

                /*sX5=sX4;
                sX4=sX3;
                sX3=sX2;
                sX2=sX1;
                sX1=mX;
                sY5=sY4;
                sY4=sY3;
                sY3=sY2;
                sY2=sY1;
                sY1=mY;*/


                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }
        public void DrawAll(){
            startScreen();
            mPath.reset();
            sidePath.reset();
            for(int i=0;i<lines.size();i++){
                //  sidePath.moveTo(lines.get(i).GetP1().x,lines.get(i).GetP1().y);
                mPath.moveTo(lines.get(i).GetP1().x,lines.get(i).GetP1().y);
                //sidePath.lineTo(lines.get(i).GetP2().x,lines.get(i).GetP2().y);
                mPath.lineTo(lines.get(i).GetP2().x,lines.get(i).GetP2().y);
                // mCanvas.drawPath(sidePath,sidePaint);
                mCanvas.drawPath(mPath,mPaint);
                mPath.reset();
                sidePath.reset();
            }
            Pra1Path.reset();
            for(int i=0;i<Pra1lines.size();i++){
                Pra1Path.moveTo(Pra1lines.get(i).GetP1().x,Pra1lines.get(i).GetP1().y);
                Pra1Path.lineTo(Pra1lines.get(i).GetP2().x,Pra1lines.get(i).GetP2().y);
                mCanvas.drawPath(Pra1Path,checkPaint);
                Pra1Path.reset();
            }
            Pra2Path.reset();
            for(int i=0;i<Pra2lines.size();i++){
                Pra2Path.moveTo(Pra2lines.get(i).GetP1().x,Pra2lines.get(i).GetP1().y);
                Pra2Path.lineTo(Pra2lines.get(i).GetP2().x,Pra2lines.get(i).GetP2().y);
                mCanvas.drawPath(Pra2Path,checkPaint);
                Pra2Path.reset();
            }
        }
        private boolean CheckLinesState(boolean TakeCare){
            if(lines.size()>1) {
                float dd = lines.get(lines.size() - 2).GetDegrees(lines.get(lines.size() - 1));
                if (lines.get(lines.size() - 2).GetDegrees(lines.get(lines.size() - 1)) < 10 || lines.get(lines.size() - 1).GetDist() < 5) {/////////////////////////////////
                    lines.remove(lines.size() - 1);
                    Pra1lines.remove(Pra1lines.size()-1);
                    Pra1lines.get(Pra1lines.size()-1).RestoreP2();
                    Pra2lines.remove(Pra2lines.size()-1);
                    Pra2lines.get(Pra2lines.size()-1).RestoreP2();
                    mPath.reset();
                    //   sidePath.reset();
                    DrawAll();
                    return false;
                }
                boolean Problamtic=false;
                for (int i = lines.size() - 2; i >= 0&&!Problamtic; i--) {
                    if (Pra1lines.get(Pra1lines.size() - 1).CheckIntersect(lines.get(i)) || Pra2lines.get(Pra2lines.size() - 1).CheckIntersect(lines.get(i))||lines.get(lines.size() - 1).CheckIntersect(lines.get(i))) {
                        Problamtic=true;
                    }
                }
                for (int i = Pra1lines.size() - 2; i >= 0&&!Problamtic; i--) {
                    if (Pra1lines.get(Pra1lines.size() - 1).CheckIntersect(Pra1lines.get(i)) || Pra2lines.get(Pra2lines.size() - 1).CheckIntersect(Pra1lines.get(i))||lines.get(lines.size() - 1).CheckIntersect(Pra1lines.get(i))) {
                        Problamtic=true;
                    }
                }
                for (int i = Pra2lines.size() - 2; i >= 0&&!Problamtic; i--) {
                    if (Pra1lines.get(Pra1lines.size() - 1).CheckIntersect(Pra2lines.get(i)) || Pra2lines.get(Pra2lines.size() - 1).CheckIntersect(Pra2lines.get(i))||lines.get(lines.size() - 1).CheckIntersect(Pra2lines.get(i))) {
                        Problamtic=true;
                    }
                }
                if(Problamtic&&TakeCare){
                    lines.remove(lines.size() - 1);
                    Pra1lines.remove(Pra1lines.size()-1);
                    Pra1lines.get(Pra1lines.size()-1).RestoreP2();
                    Pra2lines.remove(Pra2lines.size()-1);
                    Pra2lines.get(Pra2lines.size()-1).RestoreP2();
                    mPath.reset();
                    //      sidePath.reset();
                    DrawAll();
                    return false;
                }

            }
            return true;
        }
        private void ParallelFix(Line l1){
            if(Pra1lines.size()>0) {
                Line[] before=new Line[2];
                before[0]=Pra1lines.get(Pra1lines.size() - 1);
                before[1]=Pra2lines.get(Pra2lines.size() - 1);
                Line[] after=new Line[2];
                after[0]=l1.ParallelLines()[0];
                after[1]=l1.ParallelLines()[1];
                int intersectCheck=Line.WhatIsConnected(before,after);
                if(intersectCheck==-1){
                  /*  float M_before0=before[0].Get_M();
                    float N_before0=before[0].Get_N();
                    float M_before1=before[1].Get_M();
                    float N_before1=before[1].Get_N();
                    float M_after0=after[0].Get_M();
                    float N_after0=after[0].Get_N();
                    float M_after1=after[1].Get_M();
                    float N_after1=after[1].Get_N();*/
                    return;
                }
                if(intersectCheck==00){
                    Pra1lines.add(after[0]);
                    Pra2lines.add(after[1]);

                }
                if(intersectCheck==10){
                    Pra2lines.add(after[0]);

                    Pra1lines.add(after[1]);
                }
                if(intersectCheck==01){
                    Pra1lines.add(after[1]);

                    Pra2lines.add(after[0]);
                }
                if(intersectCheck==11){
                    Pra2lines.add(after[1]);

                    Pra1lines.add(after[0]);
                }
                /*if(Pra1lines.size()<2){
                    float M_before0=before[0].Get_M();
                    float N_before0=before[0].Get_N();
                    float M_before1=before[1].Get_M();
                    float N_before1=before[1].Get_N();
                    float M_after0=after[0].Get_M();
                    float N_after0=after[0].Get_N();
                    float M_after1=after[1].Get_M();
                    float N_after1=after[1].Get_N();
                    ParallelFix(l1);
                    return;
                }*/
                PointF inter1=Line.HipotaticIntersect(Pra1lines.get(Pra1lines.size()-2),Pra1lines.get(Pra1lines.size()-1));
                Pra1lines.get(Pra1lines.size()-2).SetP2(inter1,true);
                Pra1lines.get(Pra1lines.size()-1).SetP1(inter1,true);
                PointF inter2=Line.HipotaticIntersect(Pra2lines.get(Pra2lines.size()-2),Pra2lines.get(Pra2lines.size()-1));
                Pra2lines.get(Pra2lines.size()-2).SetP2(inter2,true);
                Pra2lines.get(Pra2lines.size()-1).SetP1(inter2,true);
            }else {
                Pra1lines.add(l1.ParallelLines()[0]);
                Pra2lines.add(l1.ParallelLines()[1]);
            }
        }
        public boolean CircleIt(ArrayList<Line> SomeLines,boolean Special){
            PointF[] arr = SomeLines.get(SomeLines.size() - 2).Get_This_Another_StartCirclePoints(SomeLines.get(SomeLines.size() - 1));
            boolean total_size=SomeLines.size()>1;
            boolean dist=(float) (Line.Robot_Raduis/Math.tan(Math.toRadians(SomeLines.get(SomeLines.size()-2).GetDegrees(SomeLines.get(SomeLines.size()-1))/2)))>1;
            boolean IsPossible=!SomeLines.get(SomeLines.size()-2).GetMidCircle(SomeLines.get(SomeLines.size()-1)).equals(new PointF(-1,-1));
            PointF center=SomeLines.get(SomeLines.size()-2).GetMidCircle(SomeLines.get(SomeLines.size()-1));
            if((!total_size || !IsPossible||arr[0].x == -1 || arr[1].x == -1)&&(SomeLines.get(SomeLines.size() - 2).GetDegrees(SomeLines.get(SomeLines.size() - 1))!=180)){
                dist=(float) (Line.Robot_Raduis/Math.tan(Math.toRadians(SomeLines.get(SomeLines.size()-2).GetDegrees(SomeLines.get(SomeLines.size()-1))/2)))>1;
                IsPossible=!SomeLines.get(SomeLines.size()-2).GetMidCircle(SomeLines.get(SomeLines.size()-1)).equals(new PointF(-1,-1));
                center=SomeLines.get(SomeLines.size()-2).GetMidCircle(SomeLines.get(SomeLines.size()-1));
                SomeLines.get(SomeLines.size() - 2).Get_This_Another_StartCirclePoints(SomeLines.get(SomeLines.size() - 1));
                Pra1lines.remove(Pra1lines.size()-1);
                Pra2lines.remove(Pra2lines.size()-1);
                lines.remove(lines.size()-1);
                Context context = getApplicationContext();
                CharSequence text = "Can't round";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return false;
            }
            if(total_size&& dist &&IsPossible) {
                PointF intersect=new PointF();
                Line temp=new Line(SomeLines.get(SomeLines.size()-1));
                intersect.x=SomeLines.get(SomeLines.size()-1).GetP1().x;
                intersect.y=SomeLines.get(SomeLines.size()-1).GetP1().y;
                SomeLines.remove(SomeLines.size()-1);
                SomeLines.get(SomeLines.size() - 1).SetP2(arr[0],!Special);
                //l1.SetP1(arr[1]);
                Circle c=new Circle(arr[0],arr[1],center,intersect,Line.Robot_Raduis);
                c.AddCirclePart(SomeLines);
                SomeLines.add(new Line(temp));
                SomeLines.get(SomeLines.size() - 1).SetP1(arr[1],!Special);
            }
            return true;
        }
        private void touch_up() throws InterruptedException {
            if(redoButton.contains(mX, mY)){
                redo();
                return;
            }
            if(undoButton.contains(mX, mY)){
                undo();
                return;
            }
            if(startButton.contains(mX, mY)){
                start();
                return;
            }
            if(!canvasFrame.contains(mX, mY)){
                return;
            }

            if(CheckIfClose(p1,new PointF(mX,mY))){
                if(p1.x+Ver_Hor_Tolerance>=mX&&p1.x-Ver_Hor_Tolerance<=mX){
                    mX=p1.x;
                }
                if(p1.y+Ver_Hor_Tolerance>=mY&&p1.y-Ver_Hor_Tolerance<=mY){
                    mY=p1.y;
                }
            }


            Line l1=new Line(new PointF(p1.x,p1.y),new PointF(mX,mY));
            if(l1.GetDist()<Line.Robot_Raduis*2)
                return;
            if(l1.GetDist()>0&&lines.size()>0&&(lines.get(lines.size()-1).GetDegrees(l1)>=172&&lines.get(lines.size()-1).GetDegrees(l1)<=192)){
                float recoverX= lines.get(lines.size()-1).GetP2().x;
                float recoverY=lines.get(lines.size()-1).GetP2().y;
                lines.get(lines.size()-1).GetP2().x=l1.GetP2().x;
                lines.get(lines.size()-1).GetP2().y=l1.GetP2().y;
                ParallelFix(l1);/////can be a bug
                Pra1lines.get(Pra1lines.size()-1-1).SetP2(Pra1lines.get(Pra1lines.size()-1).GetP2(),true);
                Pra2lines.get(Pra2lines.size()-1-1).SetP2(Pra2lines.get(Pra2lines.size()-1).GetP2(),true);
                Pra1lines.remove(Pra1lines.size()-1);
                Pra2lines.remove(Pra2lines.size()-1);
                if(CheckLinesState(false)){
                    p1.x=mX;
                    p1.y=mY;
                    DrawAll();
                    UpPath.reset();
                    DownPath.reset();
                    RightPath.reset();
                    LeftPath.reset();
                }//remove only half
                else {
                    lines.get(lines.size() - 1).GetP2().x = recoverX;
                    lines.get(lines.size() - 1).GetP2().y = recoverY;
                    Pra1lines.get(Pra1lines.size()-1).RestoreP2();
                    Pra2lines.get(Pra2lines.size()-1).RestoreP2();

                }
                return;
            }else {
                if (lines.size() > 0) {
                    double c=l1.GetDegrees(lines.get(lines.size()-1));
                    mX = l1.ModifyLine(lines.get(lines.size() - 1)).x;
                    mY = l1.ModifyLine(lines.get(lines.size() - 1)).y;
                } else {
                    mX = l1.ModifyLine(null).x;
                    mY = l1.ModifyLine(null).y;
                }
                l1 = new Line(new PointF(p1.x, p1.y), new PointF(mX, mY));
                lines.add(lines.size(), new Line(l1));
            }
            int sizePra1=Pra1lines.size();
            int sizePra2=Pra2lines.size();
            ParallelFix(l1);
            if(lines.size()>1){
                if(sizePra1==Pra1lines.size()||sizePra2==Pra2lines.size()){//cut himself
                    lines.remove(lines.size()-1);
                    DrawAll();
                    return;
                }
                if(!CheckLinesState(true)){
                    return;
                }
                if(!(CircleIt(lines,false)&&CircleIt(Pra1lines,true)&&CircleIt(Pra2lines,true))){
                    float check=lines.get(lines.size()-1).GetDegrees(l1);
                    p1.x=lines.get(lines.size()-1).GetP2().x;
                    p1.y=lines.get(lines.size()-1).GetP2().y;
                }else {
                    p1.x=mX;
                    p1.y=mY;
                }

            }else {
                p1.x=mX;
                p1.y=mY;
            }
            DrawAll();
            UpPath.reset();
            DownPath.reset();
            RightPath.reset();
            LeftPath.reset();
        }


        private void startScreen(){
            int buttonsPad = 30;
            int strokeWidth = 10;
            RectF redoButtonShow = new RectF(redoButton.left + buttonsPad*3/2, redoButton.top, redoButton.right - buttonsPad/2, redoButton.bottom);
            RectF startButtonShow = new RectF(startButton.left + buttonsPad, startButton.top, startButton.right - buttonsPad, startButton.bottom);
            RectF undoButtonShow = new RectF(undoButton.left + buttonsPad/2, undoButton.top, undoButton.right - buttonsPad*3/2, undoButton.bottom);
            RectF canvasFrameShow = new RectF(canvasFrame.left + strokeWidth/2, canvasFrame.top + strokeWidth/2, canvasFrame.right - strokeWidth/2, canvasFrame.bottom - strokeWidth/2);

            Paint fillPaint = new Paint();
            Paint strokePaint = new Paint();
            Paint textPaint = new Paint();

            String text;
            float textWidth;
            float textHeight;
            float textYStart;
            float textXStart;

            int roundCornerRadius = 50;

            fillPaint.setStyle(Paint.Style.FILL);
            fillPaint.setColor(Color.rgb(90, 60, 180)); //purple
            mCanvas.drawPaint(fillPaint);

            fillPaint.setColor(Color.WHITE);
            mCanvas.drawRoundRect(canvasFrameShow, roundCornerRadius, roundCornerRadius, fillPaint);

            strokePaint.setColor(Color.BLACK);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(strokeWidth);

            mCanvas.drawRoundRect(canvasFrameShow, roundCornerRadius, roundCornerRadius, fillPaint);
            mCanvas.drawRoundRect(canvasFrameShow, roundCornerRadius, roundCornerRadius, strokePaint);

            fillPaint.setColor(Color.rgb(0, 100, 230) ); //blue
            mCanvas.drawRoundRect(undoButtonShow, roundCornerRadius, roundCornerRadius, fillPaint);
            mCanvas.drawRoundRect(undoButtonShow, roundCornerRadius, roundCornerRadius, strokePaint);
            mCanvas.drawRoundRect(redoButtonShow, roundCornerRadius, roundCornerRadius, fillPaint);
            mCanvas.drawRoundRect(redoButtonShow, roundCornerRadius, roundCornerRadius, strokePaint);
            fillPaint.setColor(Color.rgb(0, 160, 40) ); //green
            mCanvas.drawRoundRect(startButtonShow, roundCornerRadius, roundCornerRadius, fillPaint);
            mCanvas.drawRoundRect(startButtonShow, roundCornerRadius, roundCornerRadius, strokePaint);

            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(100);
            //textHeight = -textPaint.ascent();
            //textPaint.setTextAlign(Paint.Align.CENTER);
            //textYStart = redoButtonShow.centerY() + (redoButtonShow.height() - textHeight)/2;
            textYStart = redoButtonShow.bottom - 45;

            text = "redo";
            textWidth = textPaint.measureText(text);
            textXStart = redoButtonShow.left + (redoButtonShow.width() - textWidth)/2;
            mCanvas.drawText(text, textXStart, textYStart, textPaint);
            text = "start";
            textWidth = textPaint.measureText(text);
            textXStart = startButtonShow.left + (startButtonShow.width() - textWidth)/2;
            mCanvas.drawText(text, textXStart, textYStart, textPaint);
            text = "undo";
            textWidth = textPaint.measureText(text);
            textXStart = undoButtonShow.left + (undoButtonShow.width() - textWidth)/2;
            mCanvas.drawText(text, textXStart, textYStart, textPaint);

            //textPaint.setColor(Color.rgb(180, 100, 255)); //purple
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(200);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            text = "ardomino";
            textWidth = textPaint.measureText(text);
            textHeight = -textPaint.ascent() + textPaint.descent();
            textXStart = (canvasFrame.width() - textWidth)/2;
            textYStart = canvasFrame.top/2 + (canvasFrame.top - textHeight)/2;
            mCanvas.drawText(text, textXStart, textYStart, textPaint);

            /*String text = "This is some text.";

            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(50 * getResources().getDisplayMetrics().density);
            textPaint.setColor(0xFF000000);

            int width = (int) textPaint.measureText(text);
            StaticLayout staticLayout = new StaticLayout(text, textPaint, (int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
            staticLayout.draw(mCanvas);*/
        }

        private void redo(){
            startScreen();
            p1 = new PointF(-1,-1);
            lines=new ArrayList<Line>();
            Pra1lines=new ArrayList<Line>();
            Pra2lines=new ArrayList<Line>();
        }

        private void undo(){
            stackEnd3();
            if(lines.size()>1) {
                for (int i = 1; i <= 101; i++) {
                    lines.remove(lines.size() - 1);
                    Pra1lines.remove(Pra1lines.size() - 1);
                    Pra2lines.remove(Pra2lines.size() - 1);
                }
                lines.get(lines.size() - 1).RestoreP2();
                Pra1lines.get(Pra1lines.size() - 1).RestoreP2();
                Pra2lines.get(Pra2lines.size() - 1).RestoreP2();
                p1.x = lines.get(lines.size() - 1).GetP2().x;
                p1.y = lines.get(lines.size() - 1).GetP2().y;
            }else {
                if (lines.size() == 1) {
                    Pra1lines.remove(Pra1lines.size() - 1);
                    Pra2lines.remove(Pra2lines.size() - 1);
                    lines.remove(lines.size() - 1);
                    p1.x = -1;
                    p1.y = -1;
                }
            }
            DrawAll();
            UpPath.reset();
            DownPath.reset();
            RightPath.reset();
            LeftPath.reset();
        }
        private  ArrayList<Integer> HelpArray() {
            if(DrawingActivity.Current_Pointer>=DrawingActivity.CurrentPrinting.size()){
                return null;
            }
            ArrayList<Integer> Returned=new ArrayList<Integer>();
            if(DrawingActivity.Current_Pointer != 0) {
                Returned.add(0, 0);
            }
            for(int i=0;i<Domino_Remain&&((Current_Pointer+i)<DrawingActivity.CurrentPrinting.size());i++){
                int insert=DrawingActivity.CurrentPrinting.get(DrawingActivity.Current_Pointer+i)[0];
                if(insert==0){
                    insert=-3;
                }
                Returned.add(Returned.size(),insert);
                insert=DrawingActivity.CurrentPrinting.get(DrawingActivity.Current_Pointer+i)[1];
                if(insert==0){
                    insert=-3;
                }
                Returned.add(Returned.size(),insert);
            }
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, String.valueOf(Returned.size()), duration);
            toast.show();
            int end=-1;
            if(Returned.size()!=(2*Domino_Remain)){
                end=-2;
            }
            Returned.add(Returned.size(),end);
            Returned.add(Returned.size(),end);
            DrawingActivity.Prev_Pointer=DrawingActivity.Current_Pointer;
            DrawingActivity.Current_Pointer+=Domino_Remain;
            Domino_Remain=30;
            return Returned;
        }

        private void startListen(){


            myRef8.addValueEventListener(new ValueEventListener() {
                int i=0;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String empty = snapshot.getValue().toString();
                    if(empty.equals("true")){
                        if(DrawingActivity.Current_Pointer < DrawingActivity.CurrentPrinting.size()) {
                            stackEnd3();
                        }

                        //myRef2.setValue(false);
                        i++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            myRef9.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String selected = snapshot.getValue().toString();
                    if(selected.equals("true")){
                        ArrayList<Integer> array= HelpArray();
                        if(array.get(array.size()-1)==-2) {
                            if(array.size()>2){
                                myRef.setValue(array);
                                myRef10.setValue(false);
                                myRef2.setValue(false);
                            }
                            Context context = getApplicationContext();
                            CharSequence text = "null array";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            puttingDia.DismissDialog();
                            return;
                        }


                        myRef.setValue(array);
                        myRef10.setValue(false);
                        myRef2.setValue(false);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        }
        private void start() throws InterruptedException {
            DrawingActivity.Domino_Remain=Line.Domino_Number;
            DrawingActivity.Current_Pointer=0;
            if(lines.size()==0){
                Context context = getApplicationContext();
                CharSequence text = "I am already in the right place, LAWYERED";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return;
            }
            ArrayList<Line> Readylines=new ArrayList<Line>();
            Readylines.add(Readylines.size(),new Line(new PointF(0,0),new PointF(0,1000*Line.Step_Len)));
            Readylines.add(Readylines.size(),new Line(new PointF(0,1000*Line.Step_Len),lines.get(0).GetP1()));
            CircleIt(Readylines,false);
            Readylines.add(Readylines.size(),lines.get(0));
            CircleIt(Readylines,false);
            for(int i=1;i<lines.size();i++){
                Readylines.add(Readylines.size(),lines.get(i));
            }
            Line.MoveMotorsWisely(lines);

            startListen();



            /*FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef15 = database.getReference("Empty");
            myRef15.setValue(false);*/
        }
        private void stackEnd3() {
            // puttingDia.DismissDialog();
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("out of domino bricks! fill the stack with domino amount:");
            //builder.setMessage("click on OK if you replaced the stack");
            //builder.setView(LayoutInflater.from(context).inflate(R.layout.stack_end, null));
            builder.setIcon(android.R.drawable.ic_dialog_alert);


            // add a radio button list
            int n = 30;
            String[] dominoBricksReplaced = new String[n];
            for (int i = 0; i < n; i++) {
                if(i == 0){
                    dominoBricksReplaced[i] = String.valueOf(n - i) + " (full stack)"; //n
                }else{
                    dominoBricksReplaced[i] = String.valueOf(n - i);
                }
            }

            int checkedItem = 0; // n
            builder.setSingleChoiceItems(dominoBricksReplaced, checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Line.Domino_Number = n - which;
                    Domino_Remain=Line.Domino_Number;
                    changed=true;
                }
            });

            // add OK and Cancel buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    puttingDia.LoadingDialog();
                    myRef9.setValue(true);
                    //startListen();
                }
            });

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    try {
                        touch_up();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
