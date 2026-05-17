package com.example.admin.controller;

import com.example.admin.entity.DictType;
import com.example.admin.entity.Result;
import com.example.admin.service.DictTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典类型管理", description = "系统字典类型维护")
@RestController
@RequestMapping("/api/dict-types")
/** 字典类型控制器 */
public class DictTypeController {

    @Autowired
    private DictTypeService dictTypeService;

    /** 查询字典类型列表 */
    @GetMapping
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result list(@RequestParam(required = false) String keyword) {
        List<DictType> list = dictTypeService.list(keyword);
        return Result.success(list);
    }

    /** 根据ID获取字典类型详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result get(@PathVariable Long id) {
        DictType dictType = dictTypeService.getById(id);
        if (dictType == null) return Result.error("字典类型不存在");
        return Result.success(dictType);
    }

    /** 根据字典类型标识获取详情 */
    @GetMapping("/dictType/{dictType}")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result getByDictType(@PathVariable String dictType) {
        DictType dt = dictTypeService.getByDictType(dictType);
        if (dt == null) return Result.error("字典类型不存在");
        return Result.success(dt);
    }

    /** 新增字典类型 */
    @PostMapping
    @PreAuthorize("hasAuthority('system:dict:create')")
    public Result add(@RequestBody DictType dictType) {
        DictType created = dictTypeService.add(dictType);
        return Result.success("新增成功", created);
    }

    /** 修改字典类型 */
    @PutMapping
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result update(@RequestBody DictType dictType) {
        DictType updated = dictTypeService.update(dictType);
        if (updated == null) return Result.error("字典类型不存在");
        return Result.success("修改成功", updated);
    }

    /** 删除字典类型 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result delete(@PathVariable Long id) {
        dictTypeService.delete(id);
        return Result.success("删除成功");
    }
}
