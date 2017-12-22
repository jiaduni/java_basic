package redis.api;

import org.nustaq.serialization.FSTConfiguration;

public final class SerializeUtil {
    private final static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();;
    
    private SerializeUtil() {
        
    }
    
    /**
     * 序列化
     * 
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) throws Exception{
        if(object == null){
            return null;
        }
        return conf.asByteArray(object);
    }

    /**
     * 反序列化
     * 
     * @param bytes
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T unserialize(byte[] bytes) throws Exception{
        if(bytes == null || bytes.length == 0){
            return null;
        }
        return (T)conf.asObject(bytes);
    }
    
}
