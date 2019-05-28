import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Object2Url {
    public static void main(String[] args) {
        User user = new User(1,"fobai");
        try {
            System.out.println(object2Url("http://github.com?id={id}&name={name}",user));
            System.out.println(object2Url("http://github.com",user));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String object2Url(String url, Object paramObj) throws Exception {
        String partternStr = "\\{([^}])*\\}";
        Pattern pattern = Pattern.compile(partternStr);
        Matcher matcher = pattern.matcher(url);
        BeanInfo beanInfo = Introspector.getBeanInfo(paramObj.getClass());
        // 现在创建 matcher 对象
        boolean match = false;
        while (matcher.find()) {
            match = true;
            String str = matcher.group().replaceAll("[\\{\\}]", "");
            url = url.replace(matcher.group(), "" + new PropertyDescriptor(str, paramObj.getClass()).getReadMethod().invoke(paramObj));
        }
        if (!match) {
            final StringBuilder builder = new StringBuilder(url);
            if (!url.contains("?")) {
                builder.append("?");
            }
            PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
            Arrays.stream(proDescrtptors).filter(g -> !g.getName().equals("class")).forEach(g -> {
                try {
                    builder.append(g.getName()).append("=").append(g.getReadMethod().invoke(paramObj)).append("&");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
            url = builder.toString();
            url = url.endsWith("&") ? url.substring(0, url.length() - 1) : url;
        }
        return url;
    }
}
class User {
    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    int id;
    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}