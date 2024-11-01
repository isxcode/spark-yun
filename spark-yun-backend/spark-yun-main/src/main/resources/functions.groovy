import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.alibaba.fastjson2.JSONPath

import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

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
    } catch (Exception ignored) {
        return '#[[get_json_value(\'' + jsonString + '\',\'' + jsonPath + '\')]]'
    }
}

def String get_regex_value(String strValue, String patternStr) {

    try {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(new String(Base64.getDecoder().decode(strValue)))
        if (matcher.find()) {
            return matcher.group(1);
        }
    } catch (Exception ignored) {
        return '#[[get_regex_value(\'' + strValue + '\',\'' + patternStr + '\')]]'
    }
}

def String get_table_value(String dataStr, Integer row, Integer col) {

    try {
        List<List<String>> data = JSON.parseObject(new String(Base64.getDecoder().decode(dataStr)), new TypeReference<List<List<String>>>() {});
        return data[row][col - 1]
    } catch (Exception e) {
        return '#[[get_table_value(\'' + dataStr + '\',' + row + ',' + col + ')]]'
    }
}