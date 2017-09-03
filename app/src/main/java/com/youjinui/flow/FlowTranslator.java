package com.youjinui.flow;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import static com.youjinui.flow.FlowStudio.Flows;
import static com.youjinui.flow.Tag.findTagByUUID;

/**
 * 플로우 고급 언어 개별/전체 번역기
 */

public class FlowTranslator extends AppCompatActivity {

    static Tag changeTag;

    final static String
            FAIL = "Flow Translate Failed",
            NO_START = "Flow Translate Failed : NO_START",
            ERROR_CONDITION = "Flow Translate Failed : ERROR_CONDITION"
    ;

    public enum TranType {
        C, JAVA, JAVASCRIPT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_translator);

        Intent intent = getIntent();

        String TranslatedCode = intent.getStringExtra("code");

        alertTranslateFailed(TranslatedCode);

        TextView TranslateCode = (TextView)findViewById(R.id.translate_code);
        TranslateCode.setText(TranslatedCode);

    }

    private void alertTranslateFailed(String failcode){
        AlertDialog.Builder builder = new AlertDialog.Builder(FlowTranslator.this);
        builder.setTitle("번역 실패");

        String message = "";

        switch (failcode){
            case NO_START:
                message = "'시작' 순서도 기호가 존재하지 않습니다.";
                break;

            case FAIL:
                message = "플로우 번역에 실패했습니다. \n순서도를 확인해주세요.";
                break;

            case ERROR_CONDITION :
                message = "'조건문' 순서도 기호가 잘못되었습니다.";
                break;

            default:
                return;
        }

        builder.setMessage(message);
        builder.show();

    }

    static String TranslateC(){ //플로우 코드 전체 번역
        String HeaderCode =
                "#include<stdio.h>" + "\n" +
                        "int main(void){" + "\n";

        String MainCode = mainCodeTranslate(TranType.C);

        if(MainCode.equals(NO_START)) return NO_START;

        return HeaderCode + MainCode + "}";
    }

    static String TranslateJava(){
        String HeaderCode;
        String MainCode;

        return null;
    }

    static String TranslateJS(){
        return mainCodeTranslate(TranType.JAVASCRIPT);
    }

    static String mainCodeTranslate(TranType type){ //각언어의 메인코드 번역
        String mainCode = "";

        FlowTag.Start startTag = null;
        boolean nostart = false;

        //시작 순서도 찾기
        Iterator<Tag> itr = Flows.iterator();
        while(itr.hasNext()){
            Tag findTag = itr.next();
            int id = findTag.GetKind();

            if(id != R.id.side_start) continue;

            startTag = (FlowTag.Start)findTag;
            nostart = true;
            break;
        }

        //시작 순서도를 찾지 못했으면 번역 실패
        if(!nostart) return NO_START;

        //순서도 순회
        Tag findTag = startTag;

        while(!findTag.GetNext().equals("")){
            findTag = findTagByUUID(findTag.GetNext());
            int id = findTag.GetKind();

            if(id == R.id.side_end) break;

            mainCode += findTranslatorById(Flows.indexOf(findTag), type) + "\n";

            if(id == R.id.side_repeat){
                mainCode += "{\n"+
                        repeatCodeTranslate((FlowTag.Repeat)findTag, type)
                        + "}\n";
            }

            if(id == R.id.side_condition){
                mainCode += "{\n"+
                        conditionCodeTranslate((FlowTag.Condition)findTag, type);

                findTag = changeTag;
            }
        }

        return mainCode;
    }

    static String repeatCodeTranslate(FlowTag.Repeat repeat, TranType type){ //반복 코드 번역
        String repeatCode = "";

        //헤더처리
        Tag headerTag = findTagByUUID(repeat.GetHeader());

        repeatCode += findTranslatorById(Flows.indexOf(headerTag), type) + "\n";

        //헤더와 연결된 순서도 번역
        Tag findTag = headerTag;

        while(!findTag.GetNext().equals("")){
            findTag = findTagByUUID(findTag.GetNext());

            repeatCode += findTranslatorById(Flows.indexOf(headerTag), type) + "\n";
        }


        return repeatCode;
    }

    static String conditionCodeTranslate(FlowTag.Condition condition, TranType type){ //조건문 코드 번역
        Tag yesTag = null, noTag = null, findTag;

        String conditionCode = "";
        String yesCode = "";
        String noCode = "";

        if(!condition.GetYesNext().equals(""))
            yesTag = findTagByUUID(condition.GetYesNext());

        if(!condition.GetNoNext().equals(""))
            noTag = findTagByUUID(condition.GetNoNext());

        if(condition.GetYesNext().equals("") || condition.GetNoNext().equals(""))
            return ERROR_CONDITION;

        //yes 번역
        yesCode += findTranslatorById(Flows.indexOf(yesTag), type) + "\n";

        findTag = yesTag;

        while(!findTag.GetNext().equals("")){ //YES 탐색
            findTag = findTagByUUID(findTag.GetNext());

            if(findTag.ConditionEndable) break;

            yesCode += findTranslatorById(Flows.indexOf(findTag), type) + "\n";
        }

        //no번역
        noCode += findTranslatorById(Flows.indexOf(noTag), type) + "\n";

        findTag = noTag;

        while(!findTag.GetNext().equals("")){ //YES 탐색
            findTag = findTagByUUID(findTag.GetNext());

            if(findTag.ConditionEndable) break;

            noCode += findTranslatorById(Flows.indexOf(findTag), type) + "\n";
        }

        conditionCode = yesCode + "}\nelse{\n"+noCode+"}\n";

        Tag returnTag;

        if(findTag.ConditionEndable)
            returnTag = findTagByUUID(findTag.GetPrev());
        else returnTag = findTag;

        changeTag = returnTag;

        return conditionCode;
    }

    static String findTranslatorById(int index, TranType type){
        Tag tag = FlowStudio.Flows.get(index);
        int id = tag.GetKind();

        switch (id){
            case R.id.side_ready :
                return ReadyTranslate(index, type);
            case R.id.side_cheory :
                return CheoryTranslate(index);
            case R.id.side_input :
                return InputTranslate(index, type);
            case R.id.side_output :
                return OutputTranslate(index, type);
            case R.id.side_repeat :
                return RepeatTranslate(index);
            case R.id.side_condition :
                return ConditionTranslate(index);
            case R.id.side_func_start :
                return funcStartTranslate(index);
            case R.id.side_func_end :
                return funcEndTranslate(index);
            case R.id.side_func_use :
                return funcUseTranslate(index);
        }

        return null;
    }


    static String ReadyTranslate(int idx, TranType type){
        FlowTag.Ready ready = (FlowTag.Ready) FlowStudio.Flows.get(idx);
        int varType = ready.VarType;
        String varName = ready.VarName;

        if(type == TranType.C) {

            switch (varType) {
                case 0:
                    return "int " + varName + ";";
                case 1:
                    return "float " + varName + ";";
                case 2:
                    return "char[] " + varName + ";";
            }
        }

        else if(type == TranType.JAVASCRIPT){
            return "var "+varName+";";
        }

        return null;
    }

    static String CheoryTranslate(int idx){
        FlowTag.Cheory cheory = (FlowTag.Cheory) FlowStudio.Flows.get(idx);
        String varList = cheory.VarList;
        String value = cheory.Value;

        return varList+" = "+value+";";
    }

    static String InputTranslate(int idx, TranType type){
        FlowTag.Input input = (FlowTag.Input) FlowStudio.Flows.get(idx);
        String scanVar = input.ScanVar;
        String scanType = GetFormatCharForC(scanVar);
        String Ampersand = "&";

        if(scanType == "%s") Ampersand = "";

        if(type == TranType.C)
        return "scanf(\""+scanType+"\","+Ampersand+scanVar+");";

        else if(type == TranType.JAVASCRIPT)
        return scanVar+ " = scanFlow();";
        //return scanVar+" = "+"prompt(\"입력 "+scanVar+"\");";

        return null;
    }

    static String OutputTranslate(int idx, TranType type){
        FlowTag.Output output = (FlowTag.Output)FlowStudio.Flows.get(idx);
        String printVar = output.PrintVar;
        String printType = GetFormatCharForC(printVar);

        if(type == TranType.C)
            return "printf(\""+printType+"\","+printVar+");";

        else if(type == TranType.JAVASCRIPT)
            return "printFlow("+printVar+");";
        //return "document.write("+printVar+"+\"<br>\");";

        return null;
    }

    static String RepeatTranslate(int idx){
        FlowTag.Repeat repeat = (FlowTag.Repeat)FlowStudio.Flows.get(idx);
        String repeatVar = repeat.RepeatVar;
        String repeatStartValue = repeat.RepeatStartValue;
        String repeatEndValue = repeat.RepeatEndValue;
        String repeatValue = repeat.RepeatValue;

        return "for("+repeatVar+"="+ repeatStartValue+"; "+repeatVar+" < "+repeatEndValue+"; "
                +repeatVar+"="+repeatVar+"+"+repeatValue+")";
    }

    static String ConditionTranslate(int idx){
        FlowTag.Condition condition = (FlowTag.Condition)FlowStudio.Flows.get(idx);
        String targetVar = condition.TargetVar;
        String conditionVar = condition.ConditionVar;
        int cond = condition.Cond;

        String condStr = "";
//"와 같은가", "와 다른가", "보다 큰가", "보다 작은가", "보다 같거나 큰가", "보다 같거나 작은가"

        switch (cond){
            case 0:
                condStr = "==";
                break;
            case 1:
                condStr = "!=";
                break;
            case 2:
                condStr = ">";
                break;
            case 3:
                condStr = "<";
                break;
            case 4:
                condStr = ">=";
                break;
            case 5:
                condStr = "<=";
                break;
        }

        return "if("+targetVar+condStr+conditionVar+")";
    }

    static String funcStartTranslate(int idx){
        FlowTag.funcStart fstart = (FlowTag.funcStart)Flows.get(idx);
        String funcName = fstart.FuncName;
        List<Integer> factorType = fstart.FactorType;
        List<String> factorName = fstart.FactorName;

        String funcType = "void";
        //나중에 연결된 funcEnd에 따라 funcType 바꾸는 기능 작성

        String factor = "";

        Iterator<Integer> typeItr = factorType.iterator();
        Iterator<String> nameItr = factorName.iterator();

        while(typeItr.hasNext() && nameItr.hasNext()){
            String ft = "";
            switch (typeItr.next()){
                case 0 :
                    ft = "int";
                    break;

                case 1 :
                    ft = "float";
                    break;

                case 2:
                    ft = "char[]";
                    break;
            }

            String fn = nameItr.next();
            factor += ft+" "+fn;

            if(typeItr.hasNext() && nameItr.hasNext()){
                factor += ",";
            }
        }

        return funcType + " " + funcName +"("+factor+")";
    }

    static String funcEndTranslate(int idx){
        FlowTag.funcEnd fend = (FlowTag.funcEnd)Flows.get(idx);
        String returnName = fend.ReturnName;

        return "return "+returnName+";";
    }

    static String funcUseTranslate(int idx){
        FlowTag.funcUse fuse = (FlowTag.funcUse)Flows.get(idx);
        String funcName = fuse.FuncName;
        String factorName = fuse.FactorName;

        return funcName+"("+factorName+")";
    }

    static String GetFormatCharForC(String var){
        Iterator<Tag> itr = Flows.iterator();

        while(itr.hasNext()){
            Tag tag = itr.next();
            int id = tag.GetKind();

            if(id == R.id.side_ready){
                FlowTag.Ready ready = (FlowTag.Ready)tag;
                String varName = ready.VarName;
                String[] vars = varName.split(",");
                boolean FindFlag = false;

                for(int i = 0; i < vars.length; i++){
                    vars[i] = vars[i].trim();
                }

                for(int i = 0; i < vars.length; i++){
                    if(vars[i].equals(var)){
                        FindFlag = true;
                        break;
                    }
                }

                if(FindFlag){
                    int varType = ready.VarType;
                    switch (varType){
                        case 0 :
                            return "%d";
                        case 1 :
                            return "%f";
                        case 2 :
                            return "%s";
                    }
                }
            }
        }

        return null;
    }
}
