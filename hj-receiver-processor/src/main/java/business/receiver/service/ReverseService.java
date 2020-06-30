package business.receiver.service;

import business.receiver.bean.ReverseBean;
import business.receiver.threadPool.ThreadPoolService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("reverseService")
@Slf4j
public class ReverseService {
    @Autowired
    private ThreadPoolService threadPoolService;
    private static Map<String, List<ReverseBean>> sendData = new HashMap();
    private static Map<String, ChannelHandlerContext> chennel = new HashMap();

    public ReverseService() {
    }

    public Map<String, Object> receiveCmdAndSendNow(String qn, String mn, String content) {
        ReverseBean bean = new ReverseBean();
        bean.setQn(qn);
        bean.setMn(mn);
        bean.setContent(content);
        bean.setCreateTime(new Date());
        Map<String, Object> map = new HashMap();
        ChannelHandlerContext ctx = chennel.get(mn);
        if (ctx != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(content + "\r\n", CharsetUtil.UTF_8));
            this.updateReverseLog(qn, mn, 1, "");
            map.put("result", 1);
            map.put("message", "反控指令发送成功");
        } else {
            sendData.computeIfAbsent(mn, (k) -> new ArrayList()).add(bean);
            map.put("result", 0);
            map.put("message", "反控指令发送暂时无法发送，进入等待队列");
        }

        return map;
    }

    public void removeCmd() {
        synchronized(sendData) {
            Iterator var2 = sendData.keySet().iterator();

            while(true) {
                List list;
                do {
                    do {
                        if (!var2.hasNext()) {
                            return;
                        }

                        String mn = (String)var2.next();
                        list = sendData.get(mn);
                    } while(list == null);
                } while(list.size() <= 0);

                Iterator it = list.iterator();

                while(it.hasNext()) {
                    ReverseBean v = (ReverseBean)it.next();
                    if ((new Date()).getTime() - v.getCreateTime().getTime() > 1800000L) {
                        it.remove();
                    }
                }
            }
        }
    }

    public void sendLeaveCmdAfterSocketAccept(final String mn, final ChannelHandlerContext ctx) {
        final List<ReverseBean> list = sendData.get(mn);
        if (list != null && list.size() > 0) {
            this.threadPoolService.getReversePool().execute(()-> {
                Iterator var1 = list.iterator();

                while(var1.hasNext()) {
                    ReverseBean v = (ReverseBean)var1.next();
                    ctx.writeAndFlush(Unpooled.copiedBuffer(v.getContent() + "\r\n", CharsetUtil.UTF_8));
                    ReverseService.this.updateReverseLog(v.getQn(), mn, 1, "");
                }

                synchronized(ReverseService.sendData) {
                    ReverseService.sendData.remove(mn);
                }
            });
        }

    }

    public void updateReverseLog(String qn, String mn, int tag, String results) {
        String sql = "UPDATE BAK_REVERSE_LOG SET UPDATE_TIME=?,TAG=?,RESULTS=? WHERE QN=? AND MN=?";
        List<Object> params = new ArrayList();
        params.add(new Date());
        params.add(tag);
        params.add(results);
        params.add(qn);
        params.add(mn);
        //TODO 111
        //this.baseDao.sqlExcute(sql, params);
    }

    public void setChennel(String mn, ChannelHandlerContext ctx) {
        chennel.put(mn, ctx);
    }
}