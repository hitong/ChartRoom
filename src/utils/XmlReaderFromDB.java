package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import other.Entity;
import other.EntityField;

public class XmlReaderFromDB {
	/**
	 * 
	 * @param xmlPath
	 * @param entityList
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static boolean readDB(String xmlPath, ArrayList<Entity> entityList) throws Exception {
		if (NullTest.isNull(xmlPath) || NullTest.isNull(entityList)) {
			return false;
		}
		SAXReader reader = new SAXReader();
		File file = new File(xmlPath);
		Document document;
		ArrayList<Entity> tmpEntities = new ArrayList<Entity>();
		try {
			document = reader.read(file);
			Element db = document.getRootElement();
			List<Element> entities = db.elements();
			for (Element entity : entities) {
				Entity tmpEntity = new Entity();
				List<Attribute> entityAttributeList = entity.attributes();
				for (Attribute attr : entityAttributeList) {
					switch (attr.getName()) {
					case "class":
						tmpEntity.setClassName(attr.getValue());

						break;
					case "table":
						tmpEntity.setTable(attr.getValue());
						break;
					default:
						throw new Exception("xml文档内容有错误");
					}
				} // entity属性获取
				List<Element> fields = entity.elements();// entity次级节点获取
				for (Element field : fields) {
					if (field.getName().equals("field")) {
						List<Attribute> fieldAttributes = field.attributes();
						EntityField tmpEntityField = new EntityField();
						for (Attribute fieldAttribute : fieldAttributes) {
							switch (fieldAttribute.getName()) {
							case "name":
								tmpEntityField.setFieldName(fieldAttribute.getValue());
								break;
							case "column":
								tmpEntityField.setColumn(fieldAttribute.getValue());
								break;
							case "type":
								tmpEntityField.setType(fieldAttribute.getValue());
								break;
							case "key":
								if (fieldAttribute.getValue().equals("true")) {
									tmpEntityField.isKey = true;
								}
								break;
							default:
								throw new Exception("xml文档内容有错误");
							}
						}
						tmpEntity.getFields().add(tmpEntityField);
					}
				} // field属性获取
				tmpEntities.add(tmpEntity);
			} // entity节点遍历结束
			entityList.addAll(tmpEntities);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	// /**
	// * 测试代码
	// * @param args
	// */
	// public static void main(String[] args){
	// ArrayList<Entity> arrayList = new ArrayList<Entity>();
	// try {
	// if(XmlReaderFromDB.readDB("src/实验四/db.xml", arrayList))
	// System.out.println(arrayList.size());
	// for(int i = 0; i < arrayList.size(); i++){
	// for(EntityField entityField: arrayList.get(i).getFields()){
	// System.out.println(entityField.fieldName);
	// System.out.println(entityField.column);
	// }
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// System.out.println(e);
	// }
	// }
}
