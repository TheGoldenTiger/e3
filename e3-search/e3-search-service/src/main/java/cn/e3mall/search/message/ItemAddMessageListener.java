package cn.e3mall.search.message;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;
/**
 * 监听商品添加消息，接收到消息后，将对应的商品信息同步到索引库
 * @author 刘金虎
 *
 */
public class ItemAddMessageListener implements MessageListener{

	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private SolrServer solrServer;
	@Override
	public void onMessage(Message message) {
		try {
			TextMessage testMessage=(TextMessage) message;
			String text = testMessage.getText();
			Long itemId=new Long(text);
			// 1、根据商品id查询商品信息。
			Thread.sleep(1000);//解决事务没提交数据库查不到商品问题
			SearchItem searchItem = itemMapper.getItemById(itemId);
			// 2、创建一SolrInputDocument对象。
			SolrInputDocument document = new SolrInputDocument();
			// 3、使用SolrServer对象写入索引库。
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			// 5、向索引库中添加文档。
			solrServer.add(document);
			solrServer.commit();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
