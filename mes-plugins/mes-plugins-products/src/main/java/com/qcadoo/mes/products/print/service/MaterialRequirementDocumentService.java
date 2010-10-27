package com.qcadoo.mes.products.print.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.lowagie.text.DocumentException;
import com.qcadoo.mes.api.DataDefinitionService;
import com.qcadoo.mes.api.Entity;
import com.qcadoo.mes.api.TranslationService;
import com.qcadoo.mes.internal.ProxyEntity;
import com.qcadoo.mes.model.types.internal.DateType;

public abstract class MaterialRequirementDocumentService {

    @Autowired
    protected TranslationService translationService;

    @Autowired
    protected DataDefinitionService dataDefinitionService;

    private static final String FILE_NAME = "MaterialRequirement";

    private static final SimpleDateFormat D_F = new SimpleDateFormat(DateType.REPORT_DATE_TIME_FORMAT);

    @Value("${reportPath}")
    private String path;

    protected final String getFileName(final Date date) {
        return path + FILE_NAME + "_" + D_F.format(date);
    }

    protected final void updateFileName(final Entity entity, final String fileName) {
        entity.setField("fileName", fileName);
        dataDefinitionService.get("products", "materialRequirement").save(entity);
    }

    protected final Map<ProxyEntity, BigDecimal> getBomSeries(final Entity entity, final List<Entity> instructions) {
        Map<ProxyEntity, BigDecimal> products = new HashedMap();
        for (Entity instruction : instructions) {
            List<Entity> bomComponents = (List<Entity>) instruction.getField("bomComponents");
            for (Entity bomComponent : bomComponents) {
                ProxyEntity product = (ProxyEntity) bomComponent.getField("product");
                if (!(Boolean) entity.getField("onlyComponents") || "component".equals(product.getField("typeOfMaterial"))) {
                    if (products.containsKey(product)) {
                        BigDecimal quantity = products.get(product);
                        quantity = ((BigDecimal) bomComponent.getField("quantity")).add(quantity);
                        products.put(product, quantity);
                    } else {
                        products.put(product, (BigDecimal) bomComponent.getField("quantity"));
                    }
                }
            }
        }
        return products;
    }

    public abstract void generateDocument(final Entity entity, final Locale locale) throws IOException, DocumentException;

}
