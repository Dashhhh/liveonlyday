package com.ejdash.esbn.MainBottomNavigationTab.MainTab_Info.clientReport;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ejdash.esbn.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.alhazmy13.wordcloud.ColorTemplate;
import net.alhazmy13.wordcloud.WordCloud;
import net.alhazmy13.wordcloud.WordCloudView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class ClientReport extends AppCompatActivity {

    private static final int DEFAULT_DATA = 0;             // columnChart 변수
    private static final int SUBCOLUMNS_DATA = 1;          // columnChart 변수
    private static final int STACKED_DATA = 2;             // columnChart 변수
    private static final int NEGATIVE_SUBCOLUMNS_DATA = 3; // columnChart 변수
    private static final int NEGATIVE_STACKED_DATA = 4;    // columnChart 변수
    WordCloudView wordCloud;                               // wordCloud 변수
    List<WordCloud> listForWordCloud;                      // wordCloud 변수
    private PieChartView chartForPie;                      // pieChart 변수
    private PieChartData dataForPie;                       // pieChart 변수
    private ColumnChartView columnChartView;               // columnChart 변수
    private ColumnChartData columnChartData;               // columnChart 변수
    private boolean hasAxes = true;                        // columnChart 변수
    private boolean hasAxesNames = true;                   // columnChart 변수
    private boolean hasLabels = false;                     // columnChart 변수
    private boolean hasLabelForSelected = false;           // columnChart 변수
    private int dataType = DEFAULT_DATA;                   // columnChart 변수
    private boolean hasLabelsForPie = false;               // pieChart 변수
    private boolean hasLabelsOutsideForPie = false;        // pieChart 변수
    private boolean hasCenterCircleForPie = false;         // pieChart 변수
    private boolean hasCenterText1ForPie = false;          // pieChart 변수
    private boolean hasCenterText2ForPie = false;          // pieChart 변수
    private boolean hasLabelForSelectedForPie = false;     // pieChart 변수
    private ArrayList<String> getKeyName;   // redis Response Data 중 Key의 이름만 저장한 리스트


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_report);

        /*
         redis에서 키-값 가져오고 JSON -> Json -> Parse 이후 차트로 데이터 넘김
         값 안에 파이차트, 막대차트, 워드클라우드 정보가 모두 들어있음
         차트별로 파싱하는 메서드로 넣은 후 결과 값 차트로 넘김
         */
        JSONObject redisKeyResponse = getRedisKeyData();

        // 하나의 메서드에서 하나의 행동만 하는게 유지관리가 쉬울 것으로 판단되서 파싱하는 메서드 따로 만듦

        // 워드클라우드 파싱 결과는 해시로 집어넣음
        //  - 해시의 Key 이름이 Label로 들어감
        //  - 해시 Key의 Value 값이 Label 의 Value로 들어감
        HashMap parseResultForWordCloudByHash = parseRedisResultForWordCloudData(redisKeyResponse);
        wordCloudInitialize(parseResultForWordCloudByHash);    // Word Cloud View 초기화

        HashMap parseResultForPie = parseRedisResultForPieData(redisKeyResponse);
        pieChartInitialize(parseResultForPie); // 파이차트 초기화 및 데이터 입력까지 하는 메서드

        columnChartInitialize();  // 막대차트 초기화 및 데이터 입력까지 하는 메서드

    }

    /**
     * @param getRedisResultJSON - Server에서 온 Resis 관련 Response 상세는 return 결과 참조
     * @return - Server에서 날라온 Key/Value가 담겨져 있다. 자료의 내용 상세 예제는 아래 참조
     * {
     * "type": "ok",
     * "result": {
     * "WakeBoard": 68593,
     * "KiteSurfing": 37438,
     * "Land Saling": 37250,
     * "StreetTricking": 37227,
     * "WingSuit": 35702,
     * "SkateBoarding": 34369,
     * "AlpineSki": 34302,
     * "BMX": 33433,
     * "WaterSkiing": 33216
     * },
     * "userid": "teamnova001",
     * "key": "esbn:teamnova001:favoritesports"
     * }
     * <p>
     * ref) json Example > "result"는 한 명의 클라이언트가 이용한 스포츠 항목 수를 나타냄
     */
    private HashMap parseRedisResultForWordCloudData(JSONObject getRedisResultJSON) {

        HashMap<String, Integer> resultParse = new HashMap<>();
        String castingJSONtoString = null;
        try {
            Log.i("redisCheck", "parseRedisResultForWordCloudData > getRedisResultJSON >  " +  getRedisResultJSON.toString());
            castingJSONtoString = getRedisResultJSON.toString();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(castingJSONtoString);
            // 채팅내용 추가
            int getValue_WakeBoard       = element.getAsJsonObject().get("result").getAsJsonObject().get("WakeBoard").getAsInt();
            int getValue_KiteSurfing     = element.getAsJsonObject().get("result").getAsJsonObject().get("KiteSurfing").getAsInt();
            int getValue_Land            = element.getAsJsonObject().get("result").getAsJsonObject().get("Land Saling").getAsInt();
            int getValue_StreetTricking  = element.getAsJsonObject().get("result").getAsJsonObject().get("StreetTricking").getAsInt();
            int getValue_WingSuit        = element.getAsJsonObject().get("result").getAsJsonObject().get("WingSuit").getAsInt();
            int getValue_SkateBoarding   = element.getAsJsonObject().get("result").getAsJsonObject().get("SkateBoarding").getAsInt();
            int getValue_AlpineSki       = element.getAsJsonObject().get("result").getAsJsonObject().get("AlpineSki").getAsInt();
            int getValue_BMX             = element.getAsJsonObject().get("result").getAsJsonObject().get("BMX").getAsInt();
            int getValue_WaterSkiing     = element.getAsJsonObject().get("result").getAsJsonObject().get("WaterSkiing").getAsInt();

            resultParse.put("WakeBoard", getValue_WakeBoard);
            resultParse.put("KiteSurfing", getValue_KiteSurfing);
            resultParse.put("Land Saling", getValue_Land);
            resultParse.put("StreetTricking", getValue_StreetTricking);
            resultParse.put("WingSuit", getValue_WingSuit);
            resultParse.put("SkateBoarding", getValue_SkateBoarding);
            resultParse.put("AlpineSki", getValue_AlpineSki);
            resultParse.put("BMX", getValue_BMX);
            resultParse.put("WaterSkiing", getValue_WaterSkiing);
            return resultParse;

        } catch (Exception e) {
            e.printStackTrace();
        }
      return resultParse;
    }


    /**
     * @param getRedisResultJSON - server에서 넘어온 데이터이며 들어있는 내용은 하단 참조
     * @return - JSON 중 "addressinfo" 만 파싱해서 들고나감
     *
          {
            "type": "ok",
            "result": {
                "WakeBoard": 68593,
                "KiteSurfing": 37438,
                "Land Saling": 37250,
                "StreetTricking": 37227,
                "WingSuit": 35702,
                "SkateBoarding": 34369,
                "AlpineSki": 34302,
                "BMX": 33433,
                "WaterSkiing": 33216
                },
            "userid": "teamnova001",
            "key": "esbn:teamnova001:favoritesports",
            "addressinfo": {
                "부산 해운대구": 1469,
                "대구 수성수": 1383,
                "서울 관악구": 987,
                "부산 사하구": 947,
                "광주 북구": 925,
                "용인 수지구": 896,
                "수원 영통구": 799,
                "서울 금천구": 743,
                "인천 계양구": 716,
                "울산 중구": 616
            }
          }

     */
    private HashMap parseRedisResultForPieData(JSONObject getRedisResultJSON) {

        HashMap<String, Float> resultParse = new HashMap<>();
        String castingJSONtoString = null;
        try {
            Log.i("redisCheck", "parseRedisResultForWordCloudData > getRedisResultJSON >  " +  getRedisResultJSON.toString());
            castingJSONtoString = getRedisResultJSON.toString();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(castingJSONtoString);
            // 채팅내용 추가
            float getValue_addressinfo01  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("부산 해운대구").getAsFloat();
            float getValue_addressinfo02  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("대구 수성수").getAsFloat();
            float getValue_addressinfo03  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("서울 관악구").getAsFloat();
            float getValue_addressinfo04  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("부산 사하구").getAsFloat();
            float getValue_addressinfo05  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("광주 북구").getAsFloat();
            float getValue_addressinfo06  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("용인 수지구").getAsFloat();
            float getValue_addressinfo07  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("수원 영통구").getAsFloat();
            float getValue_addressinfo08  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("서울 금천구").getAsFloat();
            float getValue_addressinfo09  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("인천 계양구").getAsFloat();
            float getValue_addressinfo10  = element.getAsJsonObject().get("addressinfo").getAsJsonObject().get("울산 중구").getAsFloat();

            resultParse.put("부산 해운대구", getValue_addressinfo01);
            resultParse.put("대구 수성수", getValue_addressinfo02);
            resultParse.put("서울 관악구", getValue_addressinfo03);
            resultParse.put("부산 사하구", getValue_addressinfo04);
            resultParse.put("광주 북구", getValue_addressinfo05);
            resultParse.put("용인 수지구", getValue_addressinfo06);
            resultParse.put("수원 영통구", getValue_addressinfo07);
            resultParse.put("서울 금천구", getValue_addressinfo08);
            resultParse.put("인천 계양구", getValue_addressinfo09);
            resultParse.put("울산 중구", getValue_addressinfo10);
            return resultParse;

        } catch (Exception e) {
            e.printStackTrace();
        }
      return resultParse;
    }

    /**
     * @param parseResult - 레디스 키-밸류 파싱 완료 된 해시맵
     *                    이 변수에서 뽑은 키 이름 + 해당 키 이름에 들어 있는 스코어 정보를 WordCloud Adapter에 삽입
     *                    키 이름을 따로 뽑은 이유 > 키 이름 (String 값)이 WordCloud View 위에 올라가기 때문
     *                    키에 들어있는 값은 WordCloud View 상에서 Weight 값으로 작용함
     */
    private void wordCloudInitialize(HashMap parseResult) {
        wordCloud = findViewById(R.id.wordCloud);

         /*
           해쉬 맵 내부에 Key 이름을 String으로 가져온다
            > 방송자가 어떤 종목으로 방송을 할지 정규화시켜놓지 않음
           (ex. 스피너를 통한 스포츠 종목을 '선택'하지 않고 스포츠 종목 텍스트를 '입력'함)
        */
        Set getResultHashKey = parseResult.keySet();

        Iterator getResultKey = getResultHashKey.iterator();

        getKeyName = new ArrayList<>();

        int checkLog =0;
        while (getResultKey.hasNext()) {
            getKeyName.add((String) getResultKey.next());
            Log.i("HashKeyCheck", "getKeyName > " + getKeyName.get(checkLog));
            checkLog++;
        }

        listForWordCloud = new ArrayList<>();
        for (int i = 0; i < getKeyName.size(); i++) {
            listForWordCloud
                    .add(new WordCloud(getKeyName.get(i),
                            (Integer) parseResult.get(getKeyName.get(i)
                            )));

            Log.i("WordCloud", "getKeyName.get(i) > " + getKeyName.get(i));
            Log.i("WordCloud", "(Integer) parseResult.get(getKeyName.get(i) > " + parseResult.get(getKeyName.get(i)));
        }

        WordCloudView wordCloud = findViewById(R.id.wordCloud);
        wordCloud.setDataSet(listForWordCloud);
        wordCloud.setSize(300,300);
        wordCloud.setColors(ColorTemplate.MATERIAL_COLORS);
        wordCloud.notifyDataSetChanged();

    }


    private void columnChartInitialize() {

        columnChartView = findViewById(R.id.chartColumn);
        columnChartView.setOnValueTouchListener(new ValueTouchListenerForColumn());

        generateDataForColumn();
    }

    /**
     * PHP를 이용해 사용자의 방송 및 방송 시청 이력을 카운팅한 정보를 가져온다
     * Key는 "AppName:UserID:favoritesports"
     */
    public JSONObject getRedisKeyData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new AsyncRedisGetFavoriteSports(this).execute().get(10000, TimeUnit.MILLISECONDS);
            Log.i("redisCheck", "getRedisKeyData > json > " + jsonObject);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return jsonObject;
        }

    }   // end getRedisKeyData()

    private void pieChartInitialize(HashMap parseResult) {
        chartForPie = findViewById(R.id.chartPie);
        chartForPie.setOnValueTouchListener(new ValueTouchListenerForPie());


        Set getResultHashKey = parseResult.keySet();
        Iterator getResultKey = getResultHashKey.iterator();
        getKeyName = new ArrayList<>();

        int checkLog =0;
        while (getResultKey.hasNext()) {
            getKeyName.add((String) getResultKey.next());
            Log.i("HashKeyCheck", "getKeyName > " + getKeyName.get(checkLog));
            checkLog++;
        }

        List<SliceValue> values = new ArrayList<SliceValue>();  // 파이 안에 들어갈 내용 .setLabel()
        for (int i = 0; i < getKeyName.size(); ++i) {

//            int castResultData = (int) parseResult.get(getKeyName.get(i));
            int castResultData = Math.round((Float) parseResult.get(getKeyName.get(i))) ;
            Log.i("HashKeyCheck", "getKeyName > " + castResultData);
            SliceValue sliceValue = new SliceValue((Float) parseResult.get(getKeyName.get(i)), ChartUtils.pickColor());
            sliceValue.setLabel(getKeyName.get(i)+ "\n" + castResultData);
            values.add(sliceValue);

        }
        hasCenterCircleForPie = true; // true : 그래프 가운데 뚫림 - false : 그래프가 꽉찬 원이 됨
        hasLabelsForPie = true; // true : 차트에 값이 올라옴 - false : 값 없이 색만
        hasLabelForSelectedForPie = true; // true :  - false : 그래프가 꽉찬 원이 됨
        hasCenterText2ForPie = true;  // 가운데 구멍 뚫린 곳에 텍스트 올라옴

        dataForPie = new PieChartData(values);
        dataForPie.setHasLabels(hasLabelsForPie);
        dataForPie.setHasCenterCircle(hasCenterCircleForPie);

        if (hasCenterText1ForPie) {
            dataForPie.setCenterText1("Hello!");
            // Get roboto-italic font.
            Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Italic.ttf");
            dataForPie.setCenterText1Typeface(tf);
            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            dataForPie.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        }

        if (hasCenterText2ForPie) {
            dataForPie.setCenterText2("Charts (Roboto Italic)");
            Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Italic.ttf");

            dataForPie.setCenterText2Typeface(tf);
            dataForPie.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
        }
        chartForPie.setValueSelectionEnabled(hasLabelForSelectedForPie);    // 눌렀을때 선택이 유지됨
        chartForPie.setCircleFillRatio(1.0f);
        chartForPie.startDataAnimation(); // 값이 바뀌고 나서 호출하면 부드럽게 오므려지고 펴지는 애니메이션 나옴
        chartForPie.setPieChartData(dataForPie); // 화면에 출력
    }

    private void generateDataForColumn() {    // 컬럼차트 초기 데이터 입력 메서드
        int numSubcolumns = 1;
        int numColumns = 24;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
            }

            hasLabels = true; // true : 컬럼 차트에 라벨을 붙임 - false : 라벨없이 막대기만 서있음

            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        columnChartData = new ColumnChartData(columns);

        if (hasAxes) {      // 초기 true
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {     // 초기 true
                axisX.setName("활동량");
                axisY.setName("시간대");
            }
            columnChartData.setAxisXBottom(axisX);
            columnChartData.setAxisYLeft(axisY);
        } else {
            columnChartData.setAxisXBottom(null);
            columnChartData.setAxisYLeft(null);
        }

        columnChartView.setValueSelectionEnabled(hasLabelForSelected);    // columnChartView 눌렀을 때 눌린 상태로 유지 되고 라벨이 표시 됨
        columnChartView.setColumnChartData(columnChartData);    // 준비 된 데이터 뿌림

    }

    private class ValueTouchListenerForPie implements PieChartOnValueSelectListener {       // 파이그래프 터치 리스너

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
//            float aa = value.getValue();
//            value.setLabel("asdasdasd");
            Log.i("pieChart", "onValueSelected > arcIndex > " + arcIndex);
            Log.i("pieChart", "onValueSelected > value.getLabel() > " + Arrays.toString(value.getLabelAsChars()));
            Log.i("pieChart", "onValueSelected > value.getValue() > " + value.getValue());

        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

    private class ValueTouchListenerForColumn implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            Toast.makeText(ClientReport.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

}
