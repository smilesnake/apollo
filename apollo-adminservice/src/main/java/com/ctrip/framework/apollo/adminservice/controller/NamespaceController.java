package com.ctrip.framework.apollo.adminservice.controller;

import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.service.NamespaceService;
import com.ctrip.framework.apollo.common.dto.NamespaceDTO;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.common.exception.NotFoundException;
import com.ctrip.framework.apollo.common.utils.BeanUtils;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 名称空间 Controller层
 */
@RestController
public class NamespaceController {

  private final NamespaceService namespaceService;

  public NamespaceController(final NamespaceService namespaceService) {
    this.namespaceService = namespaceService;
  }

  /**
   * 保存名称空间
   *
   * @param appId       应用id
   * @param clusterName 集群名称
   * @param dto         名称空间信息
   * @return 保存后的名称空间信息
   */
  @PostMapping("/apps/{appId}/clusters/{clusterName}/namespaces")
  public NamespaceDTO create(@PathVariable("appId") String appId,
      @PathVariable("clusterName") String clusterName,
      @Valid @RequestBody NamespaceDTO dto) {
    Namespace entity = BeanUtils.transform(Namespace.class, dto);
    // 判断名称空间是否存在
    Namespace managedEntity = namespaceService
        .findOne(appId, clusterName, entity.getNamespaceName());
    if (managedEntity != null) {
      throw new BadRequestException("namespace already exist.");
    }

    entity = namespaceService.save(entity);
    return BeanUtils.transform(NamespaceDTO.class, entity);
  }

  /**
   * 删除名称空间
   *
   * @param appId         应用id
   * @param clusterName   集群名称
   * @param namespaceName 名称空间名称
   * @param operator      操作者
   */
  @DeleteMapping("/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName:.+}")
  public void delete(@PathVariable("appId") String appId,
      @PathVariable("clusterName") String clusterName,
      @PathVariable("namespaceName") String namespaceName, @RequestParam String operator) {
    Namespace entity = namespaceService.findOne(appId, clusterName, namespaceName);
    if (entity == null) {
      throw new NotFoundException(String.format("namespace not found for %s %s %s",
          appId, clusterName, namespaceName));
    }

    // 删除名称空间
    namespaceService.deleteNamespace(entity, operator);
  }

  /**
   * 通过应用id和集群名称查询名称份之间
   *
   * @param appId       应用id
   * @param clusterName 集群名称空间
   * @return 名称空间列表
   */
  @GetMapping("/apps/{appId}/clusters/{clusterName}/namespaces")
  public List<NamespaceDTO> find(@PathVariable("appId") String appId,
      @PathVariable("clusterName") String clusterName) {
    List<Namespace> groups = namespaceService.findNamespaces(appId, clusterName);
    return BeanUtils.batchTransform(NamespaceDTO.class, groups);
  }

  /**
   * 查询指定的名称空间
   *
   * @param namespaceId 名称空间id
   * @return 名称空间信息
   */
  @GetMapping("/namespaces/{namespaceId}")
  public NamespaceDTO get(@PathVariable("namespaceId") Long namespaceId) {
    Namespace namespace = namespaceService.findOne(namespaceId);
    if (namespace == null) {
      throw new NotFoundException(String.format("namespace not found for %s", namespaceId));
    }
    return BeanUtils.transform(NamespaceDTO.class, namespace);
  }

  /**
   * 查询指定的名称空间
   *
   * @param appId         应用id
   * @param clusterName   集群名称
   * @param namespaceName 名称空间名称
   * @return 名称空间
   */
  @GetMapping("/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName:.+}")
  public NamespaceDTO get(@PathVariable("appId") String appId,
      @PathVariable("clusterName") String clusterName,
      @PathVariable("namespaceName") String namespaceName) {
    Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
    if (namespace == null) {
      throw new NotFoundException(
          String.format("namespace not found for %s %s %s", appId, clusterName, namespaceName));
    }
    return BeanUtils.transform(NamespaceDTO.class, namespace);
  }

  /**
   * 查询关联的名称空间中的公有名称空间
   *
   * @param appId         应用id
   * @param clusterName   集群名称
   * @param namespaceName 名称空间名称
   * @return 公有的名称空间信息
   */
  @GetMapping("/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/associated-public-namespace")
  public NamespaceDTO findPublicNamespaceForAssociatedNamespace(@PathVariable String appId,
      @PathVariable String clusterName, @PathVariable String namespaceName) {
    Namespace namespace = namespaceService.findPublicNamespaceForAssociatedNamespace(clusterName,
        namespaceName);

    if (namespace == null) {
      throw new NotFoundException(
          String.format("public namespace not found. namespace:%s", namespaceName));
    }

    return BeanUtils.transform(NamespaceDTO.class, namespace);
  }

  /**
   * 名称空间发布信息(cluster -> cluster有没有发布名称空间)
   *
   * @param appId 应用id
   * @return 名称空间发布信息
   */
  @GetMapping("/apps/{appId}/namespaces/publish_info")
  public Map<String, Boolean> namespacePublishInfo(@PathVariable String appId) {
    return namespaceService.namespacePublishInfo(appId);
  }


}
