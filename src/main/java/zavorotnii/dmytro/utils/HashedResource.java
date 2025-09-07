package zavorotnii.dmytro.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.core.io.Resource;

import java.security.MessageDigest;

@Getter
public class HashedResource {
    private final Resource resource;
    private final String hash;

    public HashedResource(Resource resource){
        this.resource = resource;
        this.hash = calculateContentHash(resource);
    }

    @SneakyThrows
    private String calculateContentHash(Resource resource) {
        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        return HexUtils.toHexString(digest.digest(resource.getContentAsByteArray()));
    }
}
