package com.youjinui.flow;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.youjinui.flow.FlowStudio.Flows;

/**
 * 플로우 차트 정보
 */

public class FlowTag {

    public static void findTagClassById(int id){ //id로 부터 해당 클래스 찾아주기
        switch (id) {
            case R.id.side_start:
                Start start = new Start();
                start.SetKind(id);
                Flows.add(start);
                break;

            case R.id.side_end:
                End end = new End();
                end.SetKind(id);
                Flows.add(end);
                break;

            case R.id.side_ready:
                Ready ready = new Ready();
                ready.SetKind(id);
                Flows.add(ready);
                break;

            case R.id.side_cheory:
                Cheory cheory = new Cheory();
                cheory.SetKind(id);
                Flows.add(cheory);
                break;

            case R.id.side_input:
                Input input = new Input();
                input.SetKind(id);
                Flows.add(input);
                break;

            case R.id.side_output:
                Output output = new Output();
                output.SetKind(id);
                Flows.add(output);
                break;

            case R.id.side_condition:
                Condition condition = new Condition();
                condition.SetKind(id);
                Flows.add(condition);
                break;

            case R.id.side_repeat:
                Repeat repeat = new Repeat();
                repeat.SetKind(id);
                Flows.add(repeat);
                break;

            case R.id.side_func_start:
                funcStart fstart = new funcStart();
                fstart.SetKind(id);
                Flows.add(fstart);
                break;

            case R.id.side_func_end:
                funcEnd fend = new funcEnd();
                fend.SetKind(id);
                Flows.add(fend);
                break;

            case R.id.side_func_use:
                funcUse fuse = new funcUse();
                fuse.SetKind(id);
                Flows.add(fuse);
                break;

            default:
                break;
        }
    }

    public static class Start extends Tag {
    }

    public static class End extends Tag {

    }

    public static class Ready extends Tag {
        int VarType;
        String VarName;

        Ready(){
            this.VarType = 0;
            this.VarName = "";
        }
    }

    public static class Cheory extends Tag {
        String VarList;
        String Value;

        Cheory(){
            this.VarList = "";
            this.Value = "";
        }
    }

    public static class Input extends Tag {
        String ScanVar;

        Input(){
            this.ScanVar = "";
        }
    }

    public static class Output extends Tag {
        String PrintVar;

        Output() {
            this.PrintVar = "";
        }
    }

    public static class Condition extends Tag {
        String TargetVar;
        String ConditionVar;
        int Cond;

        String YesNext;
        String NoNext;

        List<Float> YesXList;
        List<Float> YesYList;

        List<Float> NoXList;
        List<Float> NoYList;

        Path YesLine;
        Path NoLine;

        Condition(){
            this.TargetVar = "";
            this.ConditionVar = "";
            this.Cond = 0;

            this.YesNext = "";
            this.NoNext = "";
        }

        String GetYesNext(){
            return this.YesNext;
        }

        String GetNoNext(){
            return this.NoNext;
        }

        Path GetYesLinePath(){
            return this.YesLine;
        }

        Path GetNoLinePath(){
            return this.NoLine;
        }
    }

    public static class Repeat extends Tag {

        String RepeatVar;
        String RepeatStartValue;
        String RepeatEndValue;
        String RepeatValue;

        String RepeatHeader;

        Repeat(){

            this.RepeatVar = "";
            this.RepeatStartValue = "";
            this.RepeatEndValue = "";
            this.RepeatValue = "";

            this.RepeatHeader = "";
        }

        void SetHeader(String header){
            this.RepeatHeader = header;
        }

        String GetHeader(){
            return this.RepeatHeader;
        }

        float GetInternalCodeX(){
            return this.GetLeft() + FlowResolution.Repeat.marginR;
        }

        float GetInternalCodeY(){
            return this.GetTop() + FlowResolution.Repeat.titleGap + FlowResolution.Repeat.marginR;
        }
    }

    public static class funcStart extends Tag{
        String FuncName;
        List<Integer> FactorType;
        List<String> FactorName;

        funcStart(){
            this.FuncName = "";
            this.FactorType = new ArrayList<>();
            this.FactorName = new ArrayList<>();
        }

        void SetFuncName(String name){
            this.FuncName = name;
        }

        void SetFactor(int ft, String fn){
            this.FactorType.add(ft);
            this.FactorName.add(fn);
        }
    }

    public static class funcEnd extends Tag{
        String ReturnName;

        funcEnd(){
            this.ReturnName = "";
        }

        void SetReturnName(String name){
            this.ReturnName = name;
        }
    }

    public static class funcUse extends Tag{
        String FuncName;
        String FactorName;

        funcUse(){
            this.FuncName = "";
            this.FactorName = "";
        }

        void SetFuncName(String name){
            this.FuncName = name;
        }

        void SetFactorName(String name){
            this.FactorName = name;
        }
    }
}

class Tag{
    protected Path path;
    protected String text;
    protected float x, y;
    protected float width, height;

    protected int kind;

    protected String uuid;
    protected String prev;
    protected String next;

    protected Path linePath;

    protected List<Float> linePathX;
    protected List<Float> linePathY;

    protected boolean ConditionEndable;

    boolean isValidAtTouch(float x, float y){

        if( GetLeft() < x && GetRight() > x && GetTop() < y && GetBottom() > y ){ //터치가 유효하면
            return true;
        }

        return false;
    }

    void SetPrev(String prev){
        this.prev = prev;
    }

    String GetPrev(){
        return this.prev;
    }

    void SetNext(String next){
        this.next = next;
    }

    String GetNext(){
        return this.next;
    }

    void SetLinePath(Path path){
        this.linePath = path;
    }

    Path GetLinePath(){
        return this.linePath;
    }

    void SetLinePathX(List linePathX){
        this.linePathX = linePathX;
    }

    List<Float> GetLinePathX(){
        return this.linePathX;
    }

    void SetLinePathY(List linePathY){
        this.linePathY = linePathY;
    }

    List<Float> GetLinePathY(){
        return this.linePathY;
    }

    void SetUUID(String uuid){
        this.uuid = uuid;
    }

    String GetUUID(){
        return this.uuid;
    }

    void SetKind(int kind) {
        this.kind = kind;
    }

    int GetKind() {
        return this.kind;
    }

    void SetPath(Path path){
        this.path = new Path(path);
    }

    Path GetPath(){
        return this.path;
    }

    void SetText(String text){
        this.text = text;
    }

    String GetText(){
        return this.text;
    }

    void SetX(float x){
        this.x = x;
    }

    float GetX(){
        return this.x;
    }

    void SetY(float y){
        this.y = y;
    }

    float GetY() {
        return this.y;
    }

    void SetWidth(float width){
        this.width = width;
    }

    float GetWidth(){
        return this.width;
    }

    void SetHeight(float height){
        this.height = height;
    }

    float GetHeight(){
        return this.height;
    }

    float GetLeft() {
        return GetX();
    }

    float GetRight() {
        return GetX() + GetWidth();
    }

    float GetTop() {
        return GetY();
    }

    float GetBottom() {
        return GetY() + GetHeight();
    }

    float GetCenterHorizontal(){
        return GetRight() - GetWidth()/2;
    }

    float GetCenterVertical(){
        return GetBottom() - GetHeight()/2;
    }

    static Tag findTagByUUID(String uuid){
        Iterator<Tag> itr = Flows.iterator();
        while(itr.hasNext()){
            Tag tag = itr.next();
            if(tag.GetUUID().equals(uuid)) return tag;
        }

        return null;
    }
}