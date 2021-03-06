package com.hilbert.api.tree;

import com.hilbert.api.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface TreeService {
    ResponseEntity<Response<List<TreeDTO>>> findAllTrees();

    ResponseEntity<Response<TreeDTO>> getOneTreeById(Long treeId);

    ResponseEntity<Response<TreeDTO>> saveTree(TreeDTO treeDTO, BindingResult bindingResult);

    ResponseEntity<Response<TreeDTO>> updateTree(Long treeId, TreeDTO treeDTO, BindingResult bindingResult);

    ResponseEntity<Response<String>> deleteTree(Long treeId);

    ResponseEntity<Response<TreeDTO>> getOneTreeByName(String singleName);
}
