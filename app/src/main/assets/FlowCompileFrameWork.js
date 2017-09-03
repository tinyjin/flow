//FlowCompile FrameWork
var isScan = false;
var value;

function foo(arg){
    console.log("foo~"+arg);
}

function getValue(arg){
    isScan = true;
    value = arg;

    console.log("Success getValue : "+value);
}

function scanFlow(){
    window.android.scanFlow();
    while(!isScan){}

    isScan = false;
    return value;
}

function printFlow(arg){
    window.android.printFlow(arg);
    console.log("print: "+arg);
}

console.log("Start Script");

/*
window.android.scanFlow();
while(!isScan){}
입력 변수 = scanFlow();
*/

/*
1. 자바스크립트에서 입력해야 한다고 안드로이드로 전송
2. 안드로이드 감지 - 입력상태일때 EditText의 onText상태 String을 저장하고
키보드 완료버튼을 누르면 저장된거를 전송
3. 자바스크립트는 전송받을 때 까지 while문으로 계속 기다린다.
4. 입력받은 String값을 변수의 타입형으로 캐스팅하여 변수에 저장
5. 전송된 값은 플로우 콘솔에 append
*/
