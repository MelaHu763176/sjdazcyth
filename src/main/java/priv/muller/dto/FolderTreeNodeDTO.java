package priv.muller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderTreeNodeDTO {

    /**
     * 文件id
     */
    private Long id ;

    /**
     * 父文件ID
     */
    private Long parentId;


    /**
     * 文件名称
     */
    private String label;

    /**
     * 子节点列表
     */
    private List<FolderTreeNodeDTO> children = new ArrayList<>();

}