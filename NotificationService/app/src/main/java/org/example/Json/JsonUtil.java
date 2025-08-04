package org.example.Json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

//burada biz event tipini json halinde iletiyoruz ve json halinde gelen veriyi de event yapıyoruz, işte tüm mantık burada
//eventi gönderirken json'a çevirip göndermek alınca da evente çevirip almak ve kullanmak
@Component
public class JsonUtil {
    // ObjectMapperı statik olarak tanımlıyoruz,böylece her yerde aynı nesneyi direkt üstündne kullanabiliyoruz
    private static final ObjectMapper objectMapper = new ObjectMapper();
    //jsona çevirme fonksiyonumuzu yazalım
    public static String toJson(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        try {
            //objeyi direkt string'e çeviriyoruz
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw e;
        }
    }
    // statik olarak tanımladık, böylece her yerde nesne üretmeden kullanacağız
    public static ObjectMapper objectMapper() {
        return objectMapper;
    }

}
