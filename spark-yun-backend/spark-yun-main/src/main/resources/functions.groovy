import com.alibaba.fastjson.JSON
import com.alibaba.fastjson2.JSONPath

import java.text.SimpleDateFormat

def Date now() {

    return new Date()
}

def Date add_day(Date date, int days) {

    Calendar calendar = Calendar.getInstance()
    calendar.setTime(date)
    calendar.add(Calendar.DAY_OF_MONTH, days)
    return calendar.getTime()
}

def Date add_month(Date date, int months) {

    Calendar calendar = Calendar.getInstance()
    calendar.setTime(date)
    calendar.add(Calendar.MONTH, months)
    return calendar.getTime()
}

def String date_to_str(Date date, String format) {

    SimpleDateFormat sdf = new SimpleDateFormat(format)
    return sdf.format(date)
}

def String get_json_value(String jsonString, String jsonPath) {

    try {
        return String.valueOf(JSONPath.eval(JSON.parse(new String(Base64.getDecoder().decode(jsonString))), jsonPath))
    } catch (Exception e) {
        return '#[get_json_value(\'' + jsonString + '\',\'' + jsonPath + '\')]'
    }
}