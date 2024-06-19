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