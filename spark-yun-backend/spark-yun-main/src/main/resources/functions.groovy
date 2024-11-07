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

def String get_json_value(String jsonString, String jsonPath, String value) {

    try {
        return String.valueOf(JSONPath.eval(JSON.parse(new String(Base64.getDecoder().decode(jsonString))), jsonPath))
    } catch (Exception ignored) {
        print(ignored.message)
        return '空值获取'
    }
}

def String get_regex_value(String strValue, String patternStr, String value) {

    try {
        Pattern pattern = Pattern.compile(new String(Base64.getDecoder().decode(patternStr)))
        Matcher matcher = pattern.matcher(new String(Base64.getDecoder().decode(strValue)))
        if (matcher.find()) {
            return matcher.group(1);
        }
    } catch (Exception ignored) {
        print(ignored.message)
        return '空值获取'
    }
}

def String get_table_value(String dataStr, Integer row, Integer col, String value) {

    try {
        List<List<String>> data = JSON.parseObject(new String(Base64.getDecoder().decode(dataStr)), new TypeReference<List<List<String>>>() {
        });
        return data[row][col - 1]
    } catch (Exception ignored) {
        print(ignored.message)
        return '空值获取'
    }
}

def String get_json_default_value(String jsonString, String jsonPath, String value) {

    return new String(Base64.getDecoder().decode(value))
}

def String get_regex_default_value(String strValue, String patternStr, String value) {

    return new String(Base64.getDecoder().decode(value))
}

def String get_table_default_value(String dataStr, Integer row, Integer col, String value) {

    return new String(Base64.getDecoder().decode(value))
}