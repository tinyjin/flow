package com.youjinui.flow;

import static com.youjinui.flow.FlowStudio.Flows;

/**
 * 플로우 자연어 번역기
 */

public class FlowNaturalLanguage {
    static void findNaturalLanguageById(int index){
        Tag tag = Flows.get(index);
        int id = tag.GetKind();

        switch (id){
            case R.id.side_ready :
                ReadyNaturalLanguage(index);
                break;
            case R.id.side_cheory :
                CheoryNaturalLanguage(index);
                break;
            case R.id.side_input :
                InputNaturalLanguage(index);
                break;
            case R.id.side_output :
                OutputNaturalLanguage(index);
                break;
            case R.id.side_repeat :
                RepeatNautralLanguage(index);
                break;
            case R.id.side_condition :
                ConditionNaturalLanguage(index);
                break;
            case R.id.side_func_start :
                funcStartNaturalLanguage(index);
                break;
            case R.id.side_func_end :
                funcEndNaturalLanguage(index);
                break;
            case R.id.side_func_use :
                funcUseNaturalLanguage(index);
                break;
        }

    }

    static void SetNaturalLanguage(String text, int idx){ //자연어 셋팅
        Tag tag = Flows.get(idx);
        tag.SetText(text);
        FlowStudio.canvas.invalidate();
    }

    //각 도형별 자연어 생성기
    static void ReadyNaturalLanguage(int idx){
        FlowTag.Ready ready = (FlowTag.Ready) Flows.get(idx);
        String varName = ready.VarName;
        SetNaturalLanguage(varName, idx);
    }

    static void CheoryNaturalLanguage(int idx){
        FlowTag.Cheory cheory = (FlowTag.Cheory) Flows.get(idx);
        String varList = cheory.VarList;
        String value = cheory.Value;
        SetNaturalLanguage(varList+" in "+value+" assign", idx);
    }

    static void InputNaturalLanguage(int idx){
        FlowTag.Input input = (FlowTag.Input) Flows.get(idx);
        String scanVar = input.ScanVar;
        SetNaturalLanguage(scanVar + "Input ", idx);
    }

    static void OutputNaturalLanguage(int idx){
        FlowTag.Output output = (FlowTag.Output) Flows.get(idx);
        String printVar  = output.PrintVar;
        SetNaturalLanguage("Output "+printVar, idx);
    }

    static void RepeatNautralLanguage(int idx){
        FlowTag.Repeat repeat = (FlowTag.Repeat) Flows.get(idx);
        String repeatVar = repeat.RepeatVar;
        String repeatStartValue = repeat.RepeatStartValue;
        String repeatEndValue = repeat.RepeatEndValue;
        String repeatValue = repeat.RepeatValue;
        SetNaturalLanguage("Repeat "+repeatVar+": "+repeatStartValue+"~"+repeatEndValue+", "+repeatValue,idx);
    }

    static void ConditionNaturalLanguage(int idx){
        FlowTag.Condition condition = (FlowTag.Condition) Flows.get(idx);
        String targetVar = condition.TargetVar;
        String conditionVar = condition.ConditionVar;
        int cond = condition.Cond;

        String condStr = "";
//"와 같은가", "와 다른가", "보다 큰가", "보다 작은가", "보다 같거나 큰가", "보다 같거나 작은가"

        switch (cond){
            case 0:
                condStr = "=";
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
        SetNaturalLanguage(targetVar+condStr+conditionVar,idx);

    }

    static void funcStartNaturalLanguage(int idx){
        FlowTag.funcStart fstart = (FlowTag.funcStart) Flows.get(idx);

        SetNaturalLanguage("Function "+ fstart.FuncName, idx);
    }

    static void funcEndNaturalLanguage(int idx){
        FlowTag.funcEnd fend = (FlowTag.funcEnd) Flows.get(idx);

        SetNaturalLanguage(fend.ReturnName + "Return", idx);
    }

    static void funcUseNaturalLanguage(int idx){
        FlowTag.funcUse fuse = (FlowTag.funcUse) Flows.get(idx);
        SetNaturalLanguage("Function " + fuse.FuncName, idx);
    }

}
