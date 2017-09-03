package com.youjinui.flow;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.youjinui.flow.FlowCompiler.FlowConsole;
import static com.youjinui.flow.FlowCompiler.flowCompiler;
import static com.youjinui.flow.FlowCompiler.flowKeyboard;
import static com.youjinui.flow.FlowCompiler.promptScanner;

/**
 * 플로우 컴파일러
 */

public class FlowCompiler extends AppCompatActivity{
    public static LayoutInflater inflater;
    public static EditText promptScanner;

    public static TextView FlowConsole;

    public static WebView flowCompiler;

    public static InputMethodManager flowKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_compiler);

        inflater = getLayoutInflater();

        String JsHeader = "<script type = \"text/javascript\"> \n";
        String JsFooter = "</script>";

        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        String frameworkCode = null;
        try {
            inputStream = assetManager.open("FlowCompileFrameWork.js");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            frameworkCode = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }



        String compileCode = FlowTranslator.TranslateJS();
        compileCode = JsHeader + frameworkCode + compileCode + JsFooter;

        flowCompiler = (WebView)findViewById(R.id.compiler);

        flowKeyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        promptScanner = (EditText)findViewById(R.id.prompt_scanner);

        promptScanner.setBackgroundColor(Color.TRANSPARENT);
        promptScanner.setTextColor(Color.TRANSPARENT);
        promptScanner.setCursorVisible(false);

        flowCompiler.setBackgroundColor(Color.TRANSPARENT);

        FlowConsole = (TextView)findViewById(R.id.flow_console);

        final String ExternalRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        File saveFile = new File(ExternalRoot + "/.FlowTmp");

        if(!saveFile.isDirectory())
            saveFile.mkdir();

        saveFile = new File(ExternalRoot + "/.FlowTmp/tmp.html");

        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(saveFile);
            stream.write(compileCode.getBytes());
            stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        WebSettings CompilerSet = flowCompiler.getSettings();
        CompilerSet.setJavaScriptEnabled(true);

        flowCompiler.addJavascriptInterface(new FlowBridge(), "android");

        flowCompiler.setWebChromeClient(new FlowCompileClient());
        flowCompiler.loadUrl("file://"+saveFile.getPath());

        Log.v("FlowCompiler", compileCode);


    }

}

class FlowCompileClient extends WebChromeClient{

   /* @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.v("FlowCompiler",  consoleMessage.message() + '\n' + consoleMessage.messageLevel() + '\n' + consoleMessage.sourceId());
        return super.onConsoleMessage(consoleMessage);
    }
*/
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
/*
        FlowCompiler.promptScanner.setText("");
        FlowCompiler.promptScanner.requestFocus();

        flowKeyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        FlowCompiler.promptScanner.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v("FlowCompiler", "text changed!");

                FlowConsole.append(""+s.charAt(count-1));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FlowCompiler.promptScanner.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    FlowConsole.append("\n");
                    result.confirm(FlowCompiler.promptScanner.getText().toString());
                    Log.v("FlowCompiler", "result.confirm!");

                    return true;
                }

                return false;
            }
        });
*/
        return true;
    }
}

class FlowBridge {
    Handler handlerScan;
    Handler handlerPrint;
    boolean isScan;

    FlowBridge(){
        handlerScan = new Handler();
        handlerPrint = new Handler();
        isScan = false;
    }

    @JavascriptInterface
    public void scanFlow() {
        handlerScan.post(new Runnable() {
            public void run() {

                Log.v("FlowCompiler","It's Scan time!");

                isScan = true;

                promptScanner.setText("");
                promptScanner.requestFocus();

                flowKeyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                promptScanner.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if(actionId == EditorInfo.IME_ACTION_DONE && isScan){

                            Log.v("FlowCompiler", "IME_ACTION_DONE : "+promptScanner.getText().toString());

                            FlowConsole.append("\n");
                            flowCompiler.loadUrl("javascript:foo()");
                            flowCompiler.loadUrl("javascript:getValue(\"" + promptScanner.getText().toString() + "\")");
                            isScan = false;
                            return true;
                        }

                        return false;
                    }
                });

            }
        });
    }

    @JavascriptInterface
    public void printFlow(final String arg) {
        handlerPrint.post(new Runnable() {
            public void run() {
                Log.v("FlowCompiler", "It's Print time!");
                FlowConsole.append(arg + "\n");
                flowCompiler.loadUrl("javascript:foo(\"a\")");
            }
        });
    }


}
