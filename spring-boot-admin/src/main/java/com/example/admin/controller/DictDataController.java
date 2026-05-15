package com.example.admin.controller;

import com.example.admin.entity.DictData;
import com.example.admin.entity.Result;
import com.example.admin.service.DictDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典数据管理", description = "字典数据项维护")
@RestController
@RequestMapping("/api/dict-data")
public class DictDataController {

    @Autowired
    private DictDataService dictDataService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result list(@RequestParam Long dictTypeId) {
        List<DictData> list = dictDataService.listByTypeId(dictTypeId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result get(@PathVariable Long id) {
        DictData dictData = dictDataService.getById(id);
        if (dictData == null) return Result.error("字典数据不存在");
        return Result.success(dictData);
    }

    @GetMapping("/type/{dictType}")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result getByDictType(@PathVariable String dictType) {
        List<DictData> list = dictDataService.listByDictType(dictType);
        if (list == null) return Result.error("字典类型不存在");
        return Result.success(list);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:dict:create')")
    public Result add(@RequestBody DictData dictData) {
        DictData created = dictDataService.add(dictData);
        return Result.success("新增成功", created);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result update(@RequestBody DictData dictData) {
        DictData updated = dictDataService.update(dictData);
        if (updated == null) return Result.error("字典数据不存在");
        return Result.success("修改成功", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result delete(@PathVariable Long id) {
        dictDataService.delete(id);
        return Result.success("删除成功");
    }
}
