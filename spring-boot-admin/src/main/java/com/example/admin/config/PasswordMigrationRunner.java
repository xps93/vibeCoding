package com.example.admin.config;

import com.example.admin.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PasswordMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        List<Map<String, Object>> users = userMapper.selectAllPasswords();
        int migrated = 0;
        for (Map<String, Object> user : users) {
            Long id = (Long) user.get("id");
            String password = (String) user.get("password");
            if (password != null && password.startsWith("{noop}")) {
                String rawPassword = password.substring(6);
                String encoded = passwordEncoder.encode(rawPassword);
                userMapper.updatePassword(id, encoded);
                migrated++;
                log.info("Migrated password for user id={} from {{noop}} to {{bcrypt}}", id);
            }
        }
        if (migrated > 0) {
            log.info("Password migration complete: {} users upgraded from {{noop}} to BCrypt", migrated);
        }
    }
}
