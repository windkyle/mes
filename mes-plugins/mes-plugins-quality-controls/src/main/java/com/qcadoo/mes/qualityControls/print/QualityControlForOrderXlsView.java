/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 0.3.0
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */

package com.qcadoo.mes.qualityControls.print;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.qcadoo.mes.api.Entity;
import com.qcadoo.mes.qualityControls.print.utils.EntityNumberComparator;
import com.qcadoo.mes.utils.SortUtil;
import com.qcadoo.mes.utils.xls.ReportXlsView;
import com.qcadoo.mes.utils.xls.XlsUtil;

public class QualityControlForOrderXlsView extends ReportXlsView {

    @Autowired
    private QualityControlsReportService qualityControlsReportService;

    @Override
    protected final String addContent(final Map<String, Object> model, final HSSFWorkbook workbook, final Locale locale) {
        HSSFSheet sheet = workbook.createSheet(getTranslationService().translate(
                "qualityControls.qualityControlForOrder.report.title", locale));
        sheet.setZoom(4, 3);
        addOrderHeader(sheet, locale);
        addOrderSeries(model, sheet, locale);
        return getTranslationService().translate("qualityControls.qualityControlForOrder.report.fileName", locale);
    }

    private void addOrderHeader(final HSSFSheet sheet, final Locale locale) {
        HSSFRow header = sheet.createRow(0);
        HSSFCell cell0 = header.createCell(0);
        cell0.setCellValue(getTranslationService().translate("qualityControls.qualityControl.report.product.number", locale));
        cell0.setCellStyle(XlsUtil.getHeaderStyle(sheet.getWorkbook()));
        HSSFCell cell1 = header.createCell(1);
        cell1.setCellValue(getTranslationService().translate(
                "qualityControls.qualityControlsForOrder.window.qualityControlsForOrder.column.number", locale));
        cell1.setCellStyle(XlsUtil.getHeaderStyle(sheet.getWorkbook()));
        HSSFCell cell2 = header.createCell(2);
        cell2.setCellValue(getTranslationService().translate(
                "qualityControls.qualityControlForOrder.window.qualityControlForOrder.controlResult.label", locale));
        cell2.setCellStyle(XlsUtil.getHeaderStyle(sheet.getWorkbook()));
    }

    private void addOrderSeries(final Map<String, Object> model, final HSSFSheet sheet, final Locale locale) {
        int rowNum = 1;
        Map<Entity, List<Entity>> productOrders = qualityControlsReportService
                .getQualityOrdersForProduct(qualityControlsReportService.getOrderSeries(model, "qualityControlsForOrder"));
        productOrders = SortUtil.sortMapUsingComparator(productOrders, new EntityNumberComparator());
        for (Entry<Entity, List<Entity>> entry : productOrders.entrySet()) {
            List<Entity> orders = entry.getValue();
            Collections.sort(orders, new EntityNumberComparator());
            for (Entity order : orders) {
                HSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey() == null ? "" : entry.getKey().getField("number").toString());
                row.createCell(1).setCellValue(order.getField("number").toString());
                String result = "";
                if ("01correct".equals(order.getField("controlResult"))) {
                    result = getTranslationService().translate("qualityControls.qualityForOrder.controlResult.value.01correct",
                            locale);
                } else if ("02incorrect".equals(order.getField("controlResult"))) {
                    result = getTranslationService().translate("qualityControls.qualityForOrder.controlResult.value.02incorrect",
                            locale);
                } else if ("03objection".equals(order.getField("controlResult"))) {
                    result = getTranslationService().translate("qualityControls.qualityForOrder.controlResult.value.03objection",
                            locale);
                }
                row.createCell(2).setCellValue(result);
            }
        }
        sheet.autoSizeColumn((short) 0);
        sheet.autoSizeColumn((short) 1);
        sheet.autoSizeColumn((short) 2);
    }
}
