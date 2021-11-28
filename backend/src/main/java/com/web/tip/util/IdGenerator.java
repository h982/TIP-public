package com.web.tip.util;

import java.util.Random;

public class IdGenerator {
    private static Random random = new Random();

    public String generateId() {
        StringBuilder id = new StringBuilder();
        // id 가 0부터 시작하면 js 에서 server 로 id 전달시 ''추가하는 오류 발생
        id.append(random.nextInt(9) + 1);
        for (int i = 1; i < 13; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }
}
