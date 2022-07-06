package cn.fantasticmao.grpckit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * GsonUtilTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-06
 */
public class GsonUtilTest {

    @Test
    public void toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("hello", "world");
        String json = GsonUtil.GSON.toJson(map);
        Assertions.assertEquals("{\"hello\":\"world\"}", json);
    }

}
