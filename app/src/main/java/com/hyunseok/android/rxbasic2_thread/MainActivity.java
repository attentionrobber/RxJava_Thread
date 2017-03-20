package com.hyunseok.android.rxbasic2_thread;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    TextView text1, text2, text3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);


        // 실제 Task 를 처리하는 객체
        Observable<String> simpleObservable =
                Observable.create((subscriber)  -> { // 발행
                        // 네트워크를 통해서 데이터를 긁어온다
                        // 반복문을 돌면서
                        for (int i = 0; i < 10; i++) {
                            subscriber.onNext("Hello RxAndroid!!" + i); // 구독자에게 자료를 뿌려준다.
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        subscriber.onCompleted();
                    }
                );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 기본형 - Observer Subscriber를 등록해주는 함수
        simpleObservable
                .subscribeOn(Schedulers.io()) // 발행자를 별도의 SubThread 에서 동작시킨다
                .observeOn(AndroidSchedulers.mainThread()) // 구독자를 mainThread 에서 동작시킨다.
                .subscribe(new Subscriber<String>() { // Observer Subscriber(구독자)
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "[Observer1] complete!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "[Observer1] error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(String text) {
                        text1.setText("[Observer1]"+text);
                    }
                });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Observer 를 등록하는 함수 진화형(각 함수를 하나의 콜백개체에 나눠서 담아준다)
        simpleObservable
                .subscribeOn(Schedulers.io()) // 발행자를 별도의 SubThread 에서 동작시킨다
                .observeOn(AndroidSchedulers.mainThread()) // 구독자를 mainThread 에서 동작시킨다.
                .subscribe(new Action1<String>() { // onNext 함수와 동일한 역할을 하는 콜백객체
            @Override
            public void call(String s) {
                text2.setText("[Observer2]"+s);
            }
        }, new Action1<Throwable>() { // onEror 함수와 동일한 역할을 하는 콜백객체
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "[Observer2] error: " + throwable.getMessage());
            }
        }, new Action0() { // onCompleted 함수와 동일한 역할을 하는 콜백객체
            @Override
            public void call() {
                Log.d(TAG, "[Observer2] complete!");
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Observer 를 등록하는 함수 - 최종진화형(람다식)
        simpleObservable
                .subscribeOn(Schedulers.io()) // 발행자를 별도의 SubThread 에서 동작시킨다
                .observeOn(AndroidSchedulers.mainThread()) // 구독자를 mainThread 에서 동작시킨다.
                .subscribe(
                (string) -> { text3.setText("[Observer3]" + string); },
                (error) -> { Log.e(TAG, "[Observer3] error: " + error.getMessage()); },
                () -> { Log.d(TAG, "[Observer3] complete!"); }

        );
    }
}