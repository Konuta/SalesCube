/*
 *  Copyright 2009-2010 Ark Information Systems.
 */

package jp.co.arkinfosys.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.arkinfosys.common.ConfigUtil;
import jp.co.arkinfosys.common.Constants;
import jp.co.arkinfosys.common.StringUtil;
import jp.co.arkinfosys.dto.setting.FileInfoDto;
import jp.co.arkinfosys.entity.FileInfo;
import jp.co.arkinfosys.entity.join.FileInfoJoin;
import jp.co.arkinfosys.service.exception.ServiceException;
import jp.co.arkinfosys.service.exception.UnabledLockException;

import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;

/**
 * ファイル操作サービスクラスです.
 * @author Ark Information Systems
 *
 */
public class FileInfoService extends AbstractMasterEditService<FileInfoDto, FileInfoJoin>
		implements MasterSearch<FileInfoJoin> {

	/**
	 * パラメータマッピングクラスです.
	 * @author Ark Information Systems
	 *
	 */
	public static class Param {
		public static final String FILE_ID = "fileId";

		public static final String TITLE = "title";

		public static final String FILE_NAME = "fileName";

		public static final String REAL_FILE_NAME = "realFileName";

		public static final String FILE_SIZE = "fileSize";

		public static final String OPEN_LEVEL = "openLevel";

		public static final String SORT_COLUMN = "sortColumn";

		public static final String SORT_ORDER = "sortOrder";
	}

	/**
	 * アップロードファイルのプレフィックス
	 */
	private static final String FILE_PREFIX = "upload";

	/**
	 * デフォルトファイルアップロードパス
	 */
	private static final String DEFAULT_FILE_UPLOAD_PATH = "/WEB-INF/upload_files";

	@Resource
	private SeqMakerService seqMakerService;

	@Resource
	private ServletContext application;

	/**
	 *
	 * @param fileId ファイルID
	 * @return {@link FileInfoJoin}
	 * @throws ServiceException
	 * @see jp.co.arkinfosys.service.MasterSearch#findById(java.lang.String)
	 */
	@Override
	public FileInfoJoin findById(String fileId) throws ServiceException {
		try {
			Map<String, Object> params = super.createSqlParam();
			params.put(FileInfoService.Param.FILE_ID, Integer.parseInt(fileId));

			return this.selectBySqlFile(FileInfoJoin.class,
					"fileinfo/FindFileInfoById.sql", params).getSingleResult();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 *
	 * @param conditions 検索条件のマップ
	 * @return 検索結果数
	 * @throws ServiceException
	 * @see jp.co.arkinfosys.service.MasterSearch#countByCondition(java.util.Map)
	 */
	@Override
	public int countByCondition(Map<String, Object> conditions)
			throws ServiceException {
		
		return 0;
	}

	/**
	 *
	 * @param conditions 検索条件のマップ
	 * @param sortColumn ソートカラム名
	 * @param sortOrderAsc 昇順にソートするか否か
	 * @param rowCount 取得件数
	 * @param offset 取得開始位置
	 * @return {@link FileInfoJoin}のリスト
	 * @throws ServiceException
	 * @see jp.co.arkinfosys.service.MasterSearch#findByConditionLimit(java.util.Map, java.lang.String, boolean, int, int)
	 */
	@Override
	public List<FileInfoJoin> findByConditionLimit(
			Map<String, Object> conditions, String sortColumn,
			boolean sortOrderAsc, int rowCount, int offset)
			throws ServiceException {
		
		return null;
	}

	/**
	 *
	 * @param conditions 検索条件のマップ
	 * @param sortColumn ソートカラム名
	 * @param sortOrderAsc 昇順でソートするか否か
	 * @return {@link FileInfoJoin}のリスト
	 * @throws ServiceException
	 * @see jp.co.arkinfosys.service.MasterSearch#findByCondition(java.util.Map, java.lang.String, boolean)
	 */
	@Override
	public List<FileInfoJoin> findByCondition(Map<String, Object> conditions,
			String sortColumn, boolean sortOrderAsc) throws ServiceException {
		if(!conditions.containsKey(Param.OPEN_LEVEL)) {
			conditions.put(FileInfoService.Param.OPEN_LEVEL, null);
		}

		Map<String, Object> params = super.createSqlParam();
		setCondition(conditions, sortColumn, sortOrderAsc, params);

		return this.selectBySqlFile(FileInfoJoin.class,
				"fileinfo/FindFilesByOpenLevel.sql", params).getResultList();
	}

	/**
	 * ファイルIDを指定して、フォイルをダウンロードします.
	 * @param fileId ファイルID
	 * @param httpRequest {@link HttpServletRequest}
	 * @param httpResponse {@link HttpServletResponse}
	 * @throws ServiceException
	 */
	public void downloadFile(String fileId, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServiceException {
		try {
			
			FileInfo fileInfo = this.findById(fileId);
			if (fileInfo == null) {
				return;
			}

			
			String filePath = this.getUploadFolderPath() + File.separator
					+ fileInfo.realFileName;

			super.downloadFile(filePath, fileInfo.fileName, httpRequest,
					httpResponse);
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * ファイル情報を登録します.
	 * @param dto {@link FileInfoDto}
	 * @throws ServiceException
	 * @see jp.co.arkinfosys.service.AbstractMasterEditService#insertRecord(java.lang.Object)
	 */
	public void insertRecord(FileInfoDto dto) throws ServiceException {
		try {
			
			File uploadedFile = File.createTempFile(
					FileInfoService.FILE_PREFIX, "", new File(
							getUploadFolderPath()));

			BufferedInputStream is = null;
			BufferedOutputStream os = null;
			try {
				
				is = new BufferedInputStream(dto.formFile.getInputStream());
				os = new BufferedOutputStream(
						new FileOutputStream(uploadedFile));
				this.transferIO(is, os);
			} finally {
				is.close();
				os.close();
			}

			
			dto.title = dto.title;
			dto.fileName = dto.formFile.getFileName();
			dto.realFileName = uploadedFile.getName();
			dto.fileSize = String.valueOf(uploadedFile.length());

			
			long fileId = seqMakerService.nextval(FileInfo.TABLE_NAME);

			
			Map<String, Object> param = super.createSqlParam();
			param.put(FileInfoService.Param.FILE_ID, fileId);

			BeanMap deptInfo = Beans.createAndCopy(BeanMap.class, dto)
					.timestampConverter(Constants.FORMAT.TIMESTAMP)
					.dateConverter(Constants.FORMAT.DATE).includes(
							FileInfoService.Param.TITLE,
							FileInfoService.Param.FILE_NAME,
							FileInfoService.Param.REAL_FILE_NAME,
							FileInfoService.Param.FILE_SIZE,
							FileInfoService.Param.OPEN_LEVEL).execute();
			param.putAll(deptInfo);

			this.updateBySqlFile("fileinfo/InsertFileInfo.sql", param)
					.execute();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * ファイル情報を削除します.
	 * @param dto {@link FileInfoDto}
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @see jp.co.arkinfosys.service.AbstractMasterEditService#deleteRecord(java.lang.Object)
	 */
	public void deleteRecord(FileInfoDto dto) throws ServiceException,
			UnabledLockException {
		try {
			
			Map<String, Object> param = super.createSqlParam();
			param.put(FileInfoService.Param.FILE_ID, dto.fileId);
			this.lockRecordBySqlFile("fileinfo/LockFileInfoById.sql", param,
					dto.updDatetm);

			
			FileInfo fileInfo = this.findById(dto.fileId.toString());
			if (fileInfo == null) {
				return;
			}

			
			String filePath = getUploadFolderPath() + File.separator
					+ fileInfo.realFileName;

			
			param = super.createSqlParam();
			param.put(FileInfoService.Param.FILE_ID, dto.fileId);
			this.updateBySqlFile("fileinfo/DeleteFileInfoById.sql", param)
					.execute();

			
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * ファイル情報を更新します.
	 * @param dto {@link FileInfoDto}
	 * @throws Exception
	 * @see jp.co.arkinfosys.service.AbstractMasterEditService#updateRecord(java.lang.Object)
	 */
	@Override
	public void updateRecord(FileInfoDto dto) throws Exception {
		
	}

	/**
	 * 検索条件を設定します.
	 * @param conditions 検索条件値マップ
	 * @param sortColumn ソートカラム名
	 * @param sortOrderAsc 昇順でソートするか否か
	 * @param param 検索条件マップ
	 */
	private void setCondition(Map<String, Object> conditions,
			String sortColumn, boolean sortOrderAsc, Map<String, Object> param) {

		
		if (conditions.containsKey(FileInfoService.Param.OPEN_LEVEL)) {
			if (Constants.MENU_VALID_LEVEL.VALID_LIMITATION.equals(conditions
					.get(FileInfoService.Param.OPEN_LEVEL))) {
				param.put(FileInfoService.Param.OPEN_LEVEL, conditions
						.get(FileInfoService.Param.OPEN_LEVEL));
			} else if (Constants.MENU_VALID_LEVEL.VALID_FULL.equals(conditions
					.get(FileInfoService.Param.OPEN_LEVEL))) {
				param.put(FileInfoService.Param.OPEN_LEVEL, null);
			}
		}

		
		if (StringUtil.hasLength(sortColumn)) {
			param.put(FileInfoService.Param.SORT_COLUMN, StringUtil
					.convertColumnName(sortColumn));
		}

		
		if (sortOrderAsc) {
			param.put(FileInfoService.Param.SORT_ORDER, Constants.SQL.ASC);
		} else {
			param.put(FileInfoService.Param.SORT_ORDER, Constants.SQL.DESC);
		}
	}

	/**
	 * ファイルアップロードパスを返します.
	 * @return アップロードパス
	 */
	private String getUploadFolderPath() {
		String folderPath = (String) ConfigUtil
				.getConfigValue(ConfigUtil.KEY.FILE_UPLOAD_DIR_PATH);
		if (!StringUtil.hasLength(folderPath)) {
			folderPath = this.application
					.getRealPath(FileInfoService.DEFAULT_FILE_UPLOAD_PATH);
		}
		return folderPath;
	}

	/**
	 *
	 * @return {FILE_ID}
	 * @see jp.co.arkinfosys.service.AbstractMasterEditService#getKeyColumnNames()
	 */
	@Override
	protected String[] getKeyColumnNames() {
		return new String[] { "FILE_ID" };
	}

	/**
	 *
	 * @return {@link FileInfoJoin#TABLE_NAME}
	 * @see jp.co.arkinfosys.service.AbstractMasterEditService#getTableName()
	 */
	@Override
	protected String getTableName() {
		return FileInfoJoin.TABLE_NAME;
	}
}
