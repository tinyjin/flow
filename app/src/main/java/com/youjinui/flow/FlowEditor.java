package com.youjinui.flow;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

import static com.youjinui.flow.Editors.CheoryEditor.CheoryView;
import static com.youjinui.flow.Editors.CheoryEditor.GetCheoryDialog;
import static com.youjinui.flow.Editors.ConditionEditor.ConditionSelect;
import static com.youjinui.flow.Editors.ConditionEditor.ConditionView;
import static com.youjinui.flow.Editors.ConditionEditor.GetConditionDialog;
import static com.youjinui.flow.Editors.InputEditor.GetInputDialog;
import static com.youjinui.flow.Editors.InputEditor.InputAlertDialog;
import static com.youjinui.flow.Editors.InputEditor.InputView;
import static com.youjinui.flow.Editors.OutputEditor.GetOutputDialog;
import static com.youjinui.flow.Editors.OutputEditor.OutputView;
import static com.youjinui.flow.Editors.ReadyEditor.CodeArea;
import static com.youjinui.flow.Editors.ReadyEditor.GetReadyDialog;
import static com.youjinui.flow.Editors.ReadyEditor.ReadyAlertDialog;
import static com.youjinui.flow.Editors.ReadyEditor.ReadyView;
import static com.youjinui.flow.Editors.ReadyEditor.watcher;
import static com.youjinui.flow.Editors.RepeatEditor.GetRepeatDialog;
import static com.youjinui.flow.Editors.RepeatEditor.RepeatVarSelect;
import static com.youjinui.flow.Editors.RepeatEditor.RepeatView;
import static com.youjinui.flow.Editors.funcEndEditor.GetfuncEndDialog;
import static com.youjinui.flow.Editors.funcEndEditor.funcEndView;
import static com.youjinui.flow.Editors.funcStartEditor.GetfuncStartDialog;
import static com.youjinui.flow.Editors.funcStartEditor.funcStartView;
import static com.youjinui.flow.Editors.funcUseEditor.GetfuncUseDialog;
import static com.youjinui.flow.Editors.funcUseEditor.funcUseView;
import static com.youjinui.flow.FlowNaturalLanguage.findNaturalLanguageById;
import static com.youjinui.flow.FlowStudio.Flows;
import static com.youjinui.flow.FlowStudio.builder;
import static com.youjinui.flow.FlowStudio.context;
import static com.youjinui.flow.FlowStudio.infl;
import static com.youjinui.flow.FlowTranslator.findTranslatorById;

/**
 * 플로우 코드 작성창 클래스
 */

public class FlowEditor {

    public static void findEditorById(int index){ //자동으로 맞는 코드에디터 열어주는 메소드
        Tag tag = Flows.get(index);
        int id = tag.GetKind();

        switch (id) {
            case R.id.side_ready :
                GetReadyDialog(index).show();
                break;

            case R.id.side_cheory :
                GetCheoryDialog(index).show();
                break;

            case R.id.side_input :
                GetInputDialog(index).show();
                break;

            case R.id.side_output :
                GetOutputDialog(index).show();
                break;

            case R.id.side_repeat :
                GetRepeatDialog(index).show();
                break;

            case R.id.side_condition :
                GetConditionDialog(index).show();
                break;

            case R.id.side_func_start :
                GetfuncStartDialog(index).show();
                break;
            case R.id.side_func_end :
                GetfuncEndDialog(index).show();
                break;
            case R.id.side_func_use :
                GetfuncUseDialog(index).show();
                break;
        }

    }

}

class Editors {

    static class ReadyEditor { //준비 에디터, 모든 위젯은 어플 시작될 때 초기화
        static Spinner VarSelect; //변수 선택 부 위젯
        static EditText VarDeclaration; //변수 선언 부 위젯
        static TextView CodeArea;
        static View ReadyView;
        static AlertDialog ReadyAlertDialog;

        static TextWatcher watcher;

        static AlertDialog GetReadyDialog(int idx){ //다이얼로그 획득하기
            FlowTag.Ready ready = (FlowTag.Ready)Flows.get(idx);
            VarDeclaration.removeTextChangedListener(watcher);

            VarSelect.setSelection(ready.VarType);
            VarDeclaration.setText(ready.VarName);
            ReloadCode(CodeArea, idx);

            Log.v("FlowEditor","Varname: "+ready.VarName);
            SetListenerOfIndex(idx);
             //static 형 위젯에 동작부여
            return ReadyAlertDialog;
        }

         static void SetListenerOfIndex(int idx) { //동적 index 인스턴스에 데이터 저장 하는 메소드 지정
            watcher = GetTextWatcher(idx); //watcher해제를 위해 static 변수에 저장
            VarDeclaration.addTextChangedListener(watcher);

            VarSelect.setOnItemSelectedListener(GetonItemSelectedListener(idx));
         }

        private static TextWatcher GetTextWatcher(final int idx){
            return new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    FlowTag.Ready ready = (FlowTag.Ready)Flows.get(idx);
                    ready.VarName = VarDeclaration.getText().toString(); //데이터 갱신
                    ReloadCode(CodeArea, idx);
                }
                // 아래 메소드 사용 안함
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
        }

        private static AdapterView.OnItemSelectedListener GetonItemSelectedListener(final int idx){
            return new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FlowTag.Ready ready = (FlowTag.Ready)Flows.get(idx);
                    ready.VarType = position; //데이터 갱신
                    ReloadCode(CodeArea, idx);
                }
                //아래 메소드 사용 안함
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
        }
        //Ready
    }


    static class CheoryEditor {
        static Spinner VarListSelect;
        static EditText ValueSubstitution;
        static TextView CodeArea;
        static View CheoryView;
        static AlertDialog CheoryAlertDialog;

        static TextWatcher watcher;

        static AlertDialog GetCheoryDialog(int idx){ //다이얼로그 획득하기
            FlowTag.Cheory cheory = (FlowTag.Cheory)Flows.get(idx);
            ValueSubstitution.removeTextChangedListener(watcher);

            VarListSelect.setAdapter(GetAdapterForSpinner(GetVarListFromReady()));
            VarListSelect.setSelection(GetIndexFromStringArray(GetVarListFromReady(), cheory.VarList));
            ValueSubstitution.setText(cheory.Value);
            ReloadCode(CodeArea, idx);

            SetListenerOfIndex(idx);
            //static 형 위젯에 동작부여
            return CheoryAlertDialog;
        }

        static void SetListenerOfIndex(int idx) { //동적 index 인스턴스에 데이터 저장 하는 메소드 지정
            watcher = GetTextWatcher(idx); //watcher해제를 위해 static 변수에 저장
            ValueSubstitution.addTextChangedListener(watcher);

            VarListSelect.setOnItemSelectedListener(GetonItemSelectedListener(idx));
        }

        private static TextWatcher GetTextWatcher(final int idx){
            return new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    FlowTag.Cheory cheory = (FlowTag.Cheory)Flows.get(idx);
                    cheory.Value = ValueSubstitution.getText().toString(); //데이터 갱신
                    ReloadCode(CodeArea, idx);
                }
                // 아래 메소드 사용 안함
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
        }

        private static AdapterView.OnItemSelectedListener GetonItemSelectedListener(final int idx){
            return new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FlowTag.Cheory cheory = (FlowTag.Cheory)Flows.get(idx);
                    cheory.VarList = VarListSelect.getSelectedItem().toString(); //데이터 갱신
                    ReloadCode(CodeArea, idx);
                }
                //아래 메소드 사용 안함
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
        }
        //Cheory
    }

    static class InputEditor {
        static Spinner ScanVarSelect;
        static TextView CodeArea;
        static View InputView;
        static AlertDialog InputAlertDialog;

        static AlertDialog GetInputDialog(int idx){ //다이얼로그 획득하기
            FlowTag.Input input = (FlowTag.Input)Flows.get(idx);

            ScanVarSelect.setAdapter(GetAdapterForSpinner(GetVarListFromReady()));
            ScanVarSelect.setSelection(GetIndexFromStringArray(GetVarListFromReady(), input.ScanVar));

            SetListenerOfIndex(idx);
            //static 형 위젯에 동작부여
            return InputAlertDialog;
        }

        static void SetListenerOfIndex(int idx) { //동적 index 인스턴스에 데이터 저장 하는 메소드 지정
            ScanVarSelect.setOnItemSelectedListener(GetonItemSelectedListener(idx));
        }

        private static AdapterView.OnItemSelectedListener GetonItemSelectedListener(final int idx){
            return new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FlowTag.Input input = (FlowTag.Input)Flows.get(idx);
                    input.ScanVar = ScanVarSelect.getSelectedItem().toString(); //데이터 갱신
                    ReloadCode(CodeArea, idx);
                }
                //아래 메소드 사용 안함
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
        }
        //Input

    }

    static class OutputEditor {
        static Spinner PrintVarSelect;
        static TextView CodeArea;
        static View OutputView;
        static AlertDialog OutputAlertDialog;

        static AlertDialog GetOutputDialog(int idx){ //다이얼로그 획득하기
            FlowTag.Output output = (FlowTag.Output)Flows.get(idx);

            PrintVarSelect.setAdapter(GetAdapterForSpinner(GetVarListFromReady()));
            PrintVarSelect.setSelection(GetIndexFromStringArray(GetVarListFromReady(), output.PrintVar));

            SetListenerOfIndex(idx);
            //static 형 위젯에 동작부여
            return OutputAlertDialog;
        }

        static void SetListenerOfIndex(int idx) { //동적 index 인스턴스에 데이터 저장 하는 메소드 지정
            PrintVarSelect.setOnItemSelectedListener(GetonItemSelectedListener(idx));
        }

        private static AdapterView.OnItemSelectedListener GetonItemSelectedListener(final int idx) {
            return new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FlowTag.Output output = (FlowTag.Output) Flows.get(idx);
                    output.PrintVar = PrintVarSelect.getSelectedItem().toString(); //데이터 갱신
                    ReloadCode(CodeArea, idx);
                }

                //아래 메소드 사용 안함
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
        }
        //Output

    }

    static class RepeatEditor{
        static Spinner RepeatVarSelect;
        static EditText RepeatStartVar;
        static EditText RepeatEndVar;
        static EditText RepeatVar;

        static TextView CodeArea;
        static View RepeatView;
        static AlertDialog RepeatAlertDialog;

        static TextWatcher watcher;

        static AlertDialog GetRepeatDialog(int idx){ //다이얼로그 획득하기
            FlowTag.Repeat repeat = (FlowTag.Repeat)Flows.get(idx);

            RepeatStartVar.removeTextChangedListener(watcher);
            RepeatEndVar.removeTextChangedListener(watcher);
            RepeatVar.removeTextChangedListener(watcher);

            RepeatVarSelect.setAdapter(GetAdapterForSpinner(GetVarListFromReady()));
            RepeatVarSelect.setSelection(GetIndexFromStringArray(GetVarListFromReady(), repeat.RepeatValue));

            SetListenerOfIndex(idx);
            //static 형 위젯에 동작부여
            return RepeatAlertDialog;
        }

        static void SetListenerOfIndex(int idx) { //동적 index 인스턴스에 데이터 저장 하는 메소드 지정
            RepeatVarSelect.setOnItemSelectedListener(GetonItemSelectedListener(idx));
            watcher = GetTextWatcher(idx);
            RepeatStartVar.addTextChangedListener(watcher);
            RepeatEndVar.addTextChangedListener(watcher);
            RepeatVar.addTextChangedListener(watcher);
        }

        private static AdapterView.OnItemSelectedListener GetonItemSelectedListener(final int idx) {
            return new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FlowTag.Repeat repeat = (FlowTag.Repeat) Flows.get(idx);
                    repeat.RepeatVar = RepeatVarSelect.getSelectedItem().toString(); //데이터 갱신
                    ReloadCode(CodeArea, idx);
                }
                //아래 메소드 사용 안함
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
        }

        private static TextWatcher GetTextWatcher(final int idx){
            return new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    FlowTag.Repeat repeat = (FlowTag.Repeat)Flows.get(idx);
                    repeat.RepeatStartValue = RepeatStartVar.getText().toString();
                    repeat.RepeatEndValue = RepeatEndVar.getText().toString();
                    repeat.RepeatValue = RepeatVar.getText().toString();
                    ReloadCode(CodeArea, idx);
                }
                // 아래 메소드 사용 안함
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
        }
        //Repeat
    }

    static class ConditionEditor{
        static Spinner TargetVarSelect;
        static EditText ConditionVarText;
        static Spinner ConditionSelect;

        static TextView CodeArea;
        static View ConditionView;
        static AlertDialog ConditionAlertDialog;

        static TextWatcher watcher;

        static AlertDialog GetConditionDialog(int idx){
            FlowTag.Condition condition = (FlowTag.Condition)Flows.get(idx);
            TargetVarSelect.setAdapter(GetAdapterForSpinner(GetVarListFromReady()));
            TargetVarSelect.setSelection(GetIndexFromStringArray(GetVarListFromReady(), condition.TargetVar));

            ConditionVarText.removeTextChangedListener(watcher);
            ConditionSelect.setSelection(condition.Cond);

            SetListenerOfIndex(idx);

            return ConditionAlertDialog;
        }

        static void SetListenerOfIndex(int idx) { //동적 index 인스턴스에 데이터 저장 하는 메소드 지정
            TargetVarSelect.setOnItemSelectedListener(GetonItemSelectedListener(idx));
            ConditionSelect.setOnItemSelectedListener(GetonItemSelectedListener(idx));

            watcher = GetTextWatcher(idx);
            ConditionVarText.addTextChangedListener(watcher);
        }

        private static AdapterView.OnItemSelectedListener GetonItemSelectedListener(final int idx) {
            return new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FlowTag.Condition condition = (FlowTag.Condition) Flows.get(idx);

                    if(parent.equals(TargetVarSelect)){
                        condition.TargetVar = TargetVarSelect.getSelectedItem().toString();
                    }
                    else{
                        condition.Cond = position;
                    }

                    ReloadCode(CodeArea, idx);
                }
                //아래 메소드 사용 안함
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
        }

        private static TextWatcher GetTextWatcher(final int idx){
            return new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    FlowTag.Condition condition = (FlowTag.Condition)Flows.get(idx);
                    condition.ConditionVar = ConditionVarText.getText().toString();
                    ReloadCode(CodeArea, idx);
                }
                // 아래 메소드 사용 안함
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
        }
        //Condition
    }

    static class funcStartEditor{
        static EditText funcNameText;
        static Spinner FactorTypeSelect;
        static EditText FactorNameText;
        static Button FactorButton;

        static TextView CodeArea;
        static View funcStartView;
        static AlertDialog funcStartDialog;

        static TextWatcher watcher;

        static AlertDialog GetfuncStartDialog(int idx){
            FlowTag.funcStart fstart = (FlowTag.funcStart)Flows.get(idx);

            funcNameText.removeTextChangedListener(watcher);
            funcNameText.setText(fstart.FuncName);

            ReloadCode(CodeArea, idx);

            SetListenerOfIndex(idx);

            return funcStartDialog;
        }

        static void SetListenerOfIndex(int idx) { //동적 index 인스턴스에 데이터 저장 하는 메소드 지정
            watcher = GetTextWatcher(idx);
            funcNameText.addTextChangedListener(watcher);
            FactorButton.setOnClickListener(GetOnClickListener(idx));
        }

        private static View.OnClickListener GetOnClickListener(final int idx){
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlowTag.funcStart fstart = (FlowTag.funcStart)Flows.get(idx);
                    fstart.SetFactor(FactorTypeSelect.getSelectedItemPosition(), FactorNameText.getText().toString());
                    ReloadCode(CodeArea, idx);
                }
            };
        }

        private static TextWatcher GetTextWatcher(final int idx){
            return new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    FlowTag.funcStart fstart = (FlowTag.funcStart)Flows.get(idx);
                    fstart.SetFuncName(funcNameText.getText().toString());
                    ReloadCode(CodeArea, idx);
                }
                // 아래 메소드 사용 안함
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
        }

    }

    static class funcEndEditor{
        static EditText ReturnText;

        static View funcEndView;
        static AlertDialog funcEndDialog;
        static TextView CodeArea;

        static TextWatcher watcher;

        static AlertDialog GetfuncEndDialog(int idx){
            FlowTag.funcEnd fend  = (FlowTag.funcEnd)Flows.get(idx);

            ReturnText.removeTextChangedListener(watcher);
            ReturnText.setText(fend.ReturnName);

            SetListenerOfIndex(idx);

            return funcEndDialog;
        }

        private static void SetListenerOfIndex(int idx){
            watcher = GetTextWatcher(idx);
            ReturnText.addTextChangedListener(watcher);
        }

        private static TextWatcher GetTextWatcher(final int idx){
            return new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    FlowTag.funcEnd fend = (FlowTag.funcEnd)Flows.get(idx);
                    fend.SetReturnName(ReturnText.getText().toString());

                    ReloadCode(CodeArea, idx);
                }
                // 아래 메소드 사용 안함
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
        }

    }

    static class funcUseEditor{
        static Spinner FuncSelect;
        static EditText FactorText;
        static TextView CodeArea;

        static View funcUseView;
        static AlertDialog funcUseDialog;

        static TextWatcher watcher;

        static AlertDialog GetfuncUseDialog(int idx){
            FlowTag.funcUse fuse = (FlowTag.funcUse)Flows.get(idx);

            FuncSelect.setAdapter(GetAdapterForSpinner(GetFuncListFromfStart()));
            FuncSelect.setSelection(GetIndexFromStringArray(GetFuncListFromfStart(), fuse.FuncName));

            FactorText.removeTextChangedListener(watcher);
            FactorText.setText(fuse.FactorName);

            SetListenerOfIndex(idx);
            return funcUseDialog;
        }

        private static void SetListenerOfIndex(int idx){
            watcher = GetTextWatcher(idx);
            FactorText.addTextChangedListener(watcher);
            FuncSelect.setOnItemSelectedListener(GetonItemSelectedListener(idx));
        }

        private static AdapterView.OnItemSelectedListener GetonItemSelectedListener(final int idx) {
            return new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FlowTag.funcUse fuse = (FlowTag.funcUse) Flows.get(idx);

                    fuse.SetFuncName(FuncSelect.getSelectedItem().toString());

                    ReloadCode(CodeArea, idx);
                }
                //아래 메소드 사용 안함
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
        }

        private static TextWatcher GetTextWatcher(final int idx){
            return new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    FlowTag.funcUse fuse = (FlowTag.funcUse)Flows.get(idx);
                    fuse.SetFactorName(FactorText.getText().toString());

                    ReloadCode(CodeArea, idx);
                }
                // 아래 메소드 사용 안함
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
        }


    }

    //모든 위젯 초기화 FlowStudio, onCreate시 호출

    public static void InitEditorAll() {
        ReadyView = InflateEditor(R.layout.ready_dialog); //레이아웃 뷰
        ReadyEditor.VarSelect = (Spinner) ReadyView.findViewById(R.id.var_select); //자료형 스피너
        ReadyEditor.VarDeclaration = (EditText) ReadyView.findViewById(R.id.var_declaration); //변수 선언 부 에딧 텍스트
        ReadyEditor.CodeArea = (TextView) ReadyView.findViewById(R.id.code); //코드 영역
        ReadyEditor.ReadyAlertDialog = CreateAlertDialog(ReadyView); //다이얼로그
        ReadyEditor.VarSelect.setAdapter(GetAdapterForSpinner( new String[]{"Integer", "Real Type", "String"} )); //스피너 설정

        CheoryView = InflateEditor(R.layout.cheory_dialog);
        CheoryEditor.VarListSelect = (Spinner)CheoryView.findViewById(R.id.var_list);
        CheoryEditor.ValueSubstitution = (EditText)CheoryView.findViewById(R.id.value);
        CheoryEditor.CodeArea = (TextView)CheoryView.findViewById(R.id.code);
        CheoryEditor.CheoryAlertDialog = CreateAlertDialog(CheoryEditor.CheoryView);

        InputView = InflateEditor(R.layout.input_dialog);
        InputEditor.ScanVarSelect = (Spinner)InputView.findViewById(R.id.scan_var);
        InputEditor.CodeArea = (TextView)InputView.findViewById(R.id.code);
        InputEditor.InputAlertDialog = CreateAlertDialog(InputEditor.InputView);

        OutputView = InflateEditor(R.layout.output_dialog);
        OutputEditor.PrintVarSelect = (Spinner)OutputView.findViewById(R.id.print_var);
        OutputEditor.CodeArea = (TextView)OutputView.findViewById(R.id.code);
        OutputEditor.OutputAlertDialog = CreateAlertDialog(OutputEditor.OutputView);

        RepeatView = InflateEditor(R.layout.repeat_dialog);
        RepeatEditor.RepeatVarSelect = (Spinner)RepeatView.findViewById(R.id.repeat_var);
        RepeatEditor.RepeatStartVar = (EditText)RepeatView.findViewById(R.id.repeat_start_value);
        RepeatEditor.RepeatEndVar = (EditText)RepeatView.findViewById(R.id.repeat_end_value);
        RepeatEditor.RepeatVar = (EditText)RepeatView.findViewById(R.id.repeat_value);
        RepeatEditor.CodeArea = (TextView)RepeatView.findViewById(R.id.code);
        RepeatEditor.RepeatAlertDialog = CreateAlertDialog(RepeatEditor.RepeatView);

        ConditionView = InflateEditor(R.layout.condition_dialog);
        ConditionEditor.TargetVarSelect = (Spinner)ConditionView.findViewById(R.id.var_list);
        ConditionEditor.ConditionVarText = (EditText)ConditionView.findViewById(R.id.var_name);
        ConditionEditor.ConditionSelect = (Spinner)ConditionView.findViewById(R.id.condition_list);
        ConditionSelect.setAdapter(GetAdapterForSpinner( new String[]{"same?", "diffrent?", "it bigger?", "less than?", "equal or greater?", "equal or less?"}));
        ConditionEditor.CodeArea = (TextView)ConditionView.findViewById(R.id.code);
        ConditionEditor.ConditionAlertDialog = CreateAlertDialog(ConditionView);

        funcStartView = InflateEditor(R.layout.fstart_dialog);
        funcStartEditor.funcNameText = (EditText)funcStartView.findViewById(R.id.func_name);
        funcStartEditor.FactorTypeSelect = (Spinner)funcStartView.findViewById(R.id.factor_type_select);
        funcStartEditor.FactorNameText = (EditText)funcStartView.findViewById(R.id.factor_name);
        funcStartEditor.FactorButton = (Button)funcStartView.findViewById(R.id.factor_btn);
        funcStartEditor.FactorTypeSelect.setAdapter( GetAdapterForSpinner( new String[]{"Integer", "Real Type", "String"} ) );
        funcStartEditor.CodeArea = (TextView)funcStartView.findViewById(R.id.code);
        funcStartEditor.funcStartDialog = CreateAlertDialog(funcStartView);

        funcEndView = InflateEditor(R.layout.fend_dialog);
        funcEndEditor.ReturnText = (EditText)funcEndView.findViewById(R.id.returns);
        funcEndEditor.CodeArea = (TextView)funcEndView.findViewById(R.id.code);
        funcEndEditor.funcEndDialog = CreateAlertDialog(funcEndView);

        funcUseView = InflateEditor(R.layout.fuse_dialog);
        funcUseEditor.FuncSelect = (Spinner)funcUseView.findViewById(R.id.func_list);
        funcUseEditor.FactorText = (EditText)funcUseView.findViewById(R.id.factors);
        funcUseEditor.CodeArea = (TextView)funcUseView.findViewById(R.id.code);
        funcUseEditor.funcUseDialog = CreateAlertDialog(funcUseView);
    }

    //기타 부가 기능 메소드

    private static int GetIndexFromStringArray(String[] items, String item){
        for(int i = 0; i < items.length; i++){
            if(items[i].equals(item)) return i;
        }

        return 0;
    }

    private static String[] GetFuncListFromfStart(){
        Iterator<Tag> itr = Flows.iterator();
        String func = "";

        while(itr.hasNext()){
            Tag tag = itr.next();
            int id = tag.GetKind();

            if(id != R.id.side_func_start) continue;

            FlowTag.funcStart fstart = (FlowTag.funcStart)tag;
            func += fstart.FuncName + ",";
        }

        return func.split(",");
    }

    private static String[] GetVarListFromReady(){
        Iterator<Tag> itr = Flows.iterator();
        String vars = "";


        while(itr.hasNext()){
            Tag tag = itr.next();
            int id = tag.GetKind();

            if(id == R.id.side_ready){
                FlowTag.Ready ready = (FlowTag.Ready)tag;
                String varName = ready.VarName;
                vars += varName.replaceAll(" ","")+",";
            }
        }
        return vars.split(",");
    }

    private static ArrayAdapter<String> GetAdapterForSpinner(String[] items){
        return new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
    }

    private static void ReloadCode(TextView codeArea, int idx){ //코드 번역 -> 자연어 생성
        FlowTranslator.TranType tranType = FlowTranslator.TranType.C;

        codeArea.setText(findTranslatorById(idx, tranType));
        ReloadNaturalCode(idx);
    }

    private static void ReloadNaturalCode(int idx){
        findNaturalLanguageById(idx);
    }

    //아래는 위젯 별 초기화 함수 구현 예정

    //기본 다이얼로그 반환, 레이아웃 인플레이팅 함수

    private static AlertDialog CreateAlertDialog(View v){
        AlertDialog dialog = builder.create();
        dialog.setView(v);

        return dialog;
    }

    private static View InflateEditor(int LayoutId){ //레이아웃 -> 뷰 인플레이트 메소드
        return infl.inflate(LayoutId, null);
    }
}