package com.hilbert.api.tree;

import com.hilbert.api.reference.Reference;
import com.hilbert.api.reference.ReferenceRepository;
import com.hilbert.api.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TreeServiceImpl implements TreeService{

    private final TreeRepository treeRepository;
    private final ReferenceRepository referenceRepository;

    public TreeServiceImpl(TreeRepository treeRepository, ReferenceRepository referenceRepository) {
        this.treeRepository = treeRepository;
        this.referenceRepository = referenceRepository;
    }

    @Override
    public ResponseEntity<Response<List<TreeDTO>>> findAllTrees() {

        Response<List<TreeDTO>> response = new Response<>();

        List<Tree> trees = treeRepository.findAll();

        List<TreeDTO> dto = new ArrayList<>();

        trees.forEach(i -> dto.add(this.convertEntityToDto(i)));

        response.setData(dto);

        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<Response<TreeDTO>> getOneTreeById(Long treeId) {
        Response<TreeDTO> response = new Response<>();

        Optional<Tree> optionalTree = treeRepository.findById(treeId);

        if (!optionalTree.isPresent()) {
            ObjectError objectError = new ObjectError("Tree",
                    "Árvore/arbusto com id " + treeId + " não encontrado");
            response.getErrors().add(objectError.getDefaultMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        TreeDTO treeDTO = convertEntityToDto(optionalTree.get());
        response.setData(treeDTO);
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<Response<TreeDTO>> saveTree(TreeDTO treeDTO, BindingResult bindingResult) {
        Response<TreeDTO> response = new Response<>();

        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));

            return ResponseEntity.badRequest().body(response);
        }

        Boolean existsSingleName = treeRepository.existsBySingleName(treeDTO.getSingleName());

        if (existsSingleName){
            ObjectError objectError = new ObjectError("Tree",
                    "Árvore/arbusto com nome " + treeDTO.getSingleName() + " já existe na base de dados");
            response.getErrors().add(objectError.getDefaultMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        for ( Reference r : treeDTO.getReferences() ){
            if (r.getId() == null ){
                Reference reference = referenceRepository.save(r);
                r.setId(reference.getId());
            }
        }

        Tree tree = treeRepository.save(convertDtoToEntity(treeDTO));

        response.setData(convertEntityToDto(tree));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Response<TreeDTO>> updateTree(Long id, TreeDTO treeDTO, BindingResult bindingResult) {
        Response<TreeDTO> response = new Response<>();

        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));
            return ResponseEntity.ok().body(response);
        }

        Optional<Tree> existsTree = treeRepository.findById(id);

        if (!existsTree.isPresent() || !Objects.equals(treeDTO.getSingleName(), existsTree.get().getSingleName())) {
            ObjectError objectError = new ObjectError("Tree",
                    "Árvore/arbusto com id " + id + " não encontrado ou tentando modificar o nome");
            response.getErrors().add(objectError.getDefaultMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        treeDTO.setId(existsTree.get().getId());
        Tree tree = treeRepository.save(convertDtoToEntity(treeDTO));

        response.setData(convertEntityToDto(tree));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Response<String>> deleteTree(Long treeId) {
        Response<String> response = new Response<String>();

        boolean existsTree = treeRepository.existsById(treeId);

        if (!existsTree){
            response.getErrors().add("Árvore/arbusto de id " + treeId + " não encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        treeRepository.delete(treeRepository.findById(treeId).get());

        response.setData("Árvore/arbusto de id " + treeId + " apagada com sucesso");

        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<Response<TreeDTO>> getOneTreeByName(String singleName) {
        Response<TreeDTO> response = new Response<>();

        Optional<Tree> optionalTree = treeRepository.findBySingleName(singleName);

        if (!optionalTree.isPresent()) {
            ObjectError objectError = new ObjectError("Tree",
                    "Árvore/arbusto com nome " + singleName + " não encontrado");
            response.getErrors().add(objectError.getDefaultMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        TreeDTO treeDTO = convertEntityToDto(optionalTree.get());
        response.setData(treeDTO);
        return ResponseEntity.ok().body(response);
    }

    private TreeDTO convertEntityToDto(Tree tree) {
        TreeDTO dto = new TreeDTO();
        dto.setId(tree.getId());
        dto.setSingleName(tree.getSingleName());
        dto.setPopularName(tree.getPopularName());
        dto.setFamily(tree.getFamily());
        dto.setBotanicalName(tree.getBotanicalName());
        dto.setNameMeaning(tree.getNameMeaning());
        dto.setGeneralDescription(tree.getGeneralDescription());
        dto.setSpecialDescription(tree.getSpecialDescription());
        dto.setWhereOccurs(tree.getWhereOccurs());
        dto.setEcologicalInfo(tree.getEcologicalInfo());
        dto.setPhenologicalInfo(tree.getPhenologicalInfo());
        dto.setPropagation(tree.getPropagation());
        dto.setManagementGuide(tree.getManagementGuide());
        dto.setUtilities(tree.getUtilities());
        dto.setCulturalImportance(tree.getCulturalImportance());
        dto.setReferences(tree.getReferences());

        return dto;
    }

    private Tree convertDtoToEntity(TreeDTO treeDTO){
        Tree tree = new Tree();
        tree.setId(treeDTO.getId());
        tree.setSingleName(treeDTO.getSingleName());
        tree.setPopularName(treeDTO.getPopularName());
        tree.setFamily(treeDTO.getFamily());
        tree.setBotanicalName(treeDTO.getBotanicalName());
        tree.setNameMeaning(treeDTO.getNameMeaning());
        tree.setGeneralDescription(treeDTO.getGeneralDescription());
        tree.setSpecialDescription(treeDTO.getSpecialDescription());
        tree.setWhereOccurs(treeDTO.getWhereOccurs());
        tree.setEcologicalInfo(treeDTO.getEcologicalInfo());
        tree.setPhenologicalInfo(treeDTO.getPhenologicalInfo());
        tree.setPropagation(treeDTO.getPropagation());
        tree.setManagementGuide(treeDTO.getManagementGuide());
        tree.setUtilities(treeDTO.getUtilities());
        tree.setCulturalImportance(treeDTO.getCulturalImportance());
        tree.setReferences(treeDTO.getReferences());

        return tree;
    }

    private void updateTreeRecord(Tree originalTreeToBeUpdated, TreeDTO treeDTOUpdated, Long id) {
        originalTreeToBeUpdated.setId(id);
        originalTreeToBeUpdated.setSingleName(treeDTOUpdated.getSingleName());
        originalTreeToBeUpdated.setFamily(treeDTOUpdated.getFamily());
        originalTreeToBeUpdated.setFamily(treeDTOUpdated.getFamily());
        originalTreeToBeUpdated.setBotanicalName(treeDTOUpdated.getBotanicalName());
        originalTreeToBeUpdated.setNameMeaning(treeDTOUpdated.getNameMeaning());
        originalTreeToBeUpdated.setGeneralDescription(treeDTOUpdated.getGeneralDescription());
        originalTreeToBeUpdated.setSpecialDescription(treeDTOUpdated.getSpecialDescription());
        originalTreeToBeUpdated.setWhereOccurs(treeDTOUpdated.getWhereOccurs());
        originalTreeToBeUpdated.setEcologicalInfo(treeDTOUpdated.getEcologicalInfo());
        originalTreeToBeUpdated.setPhenologicalInfo(treeDTOUpdated.getPhenologicalInfo());
        originalTreeToBeUpdated.setPropagation(treeDTOUpdated.getPropagation());
        originalTreeToBeUpdated.setManagementGuide(treeDTOUpdated.getManagementGuide());
        originalTreeToBeUpdated.setUtilities(treeDTOUpdated.getUtilities());
        originalTreeToBeUpdated.setCulturalImportance(treeDTOUpdated.getCulturalImportance());
        originalTreeToBeUpdated.setReferences(treeDTOUpdated.getReferences());
    }
}
