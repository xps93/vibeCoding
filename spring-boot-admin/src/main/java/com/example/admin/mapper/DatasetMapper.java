package com.example.admin.mapper;

import com.example.admin.entity.Dataset;
import com.example.admin.entity.DatasetRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DatasetMapper {

    int insert(Dataset entity);

    List<Dataset> selectByUserId(@Param("userId") Long userId);

    Dataset selectById(@Param("id") Long id);

    Dataset selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    int deleteById(@Param("id") Long id);

    int insertRow(DatasetRow row);

    int insertRows(@Param("rows") List<DatasetRow> rows);

    List<DatasetRow> selectRowsByDatasetId(@Param("datasetId") Long datasetId);

    int deleteRowsByDatasetId(@Param("datasetId") Long datasetId);

    int countRowsByDatasetId(@Param("datasetId") Long datasetId);
}
