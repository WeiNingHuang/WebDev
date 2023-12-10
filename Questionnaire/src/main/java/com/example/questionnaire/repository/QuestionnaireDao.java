package com.example.questionnaire.repository;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.questionnaire.entity.Questionnaire;
import com.example.questionnaire.vo.QnQuVo;

@Repository
public interface QuestionnaireDao extends JpaRepository<Questionnaire, Integer> {

	// 刪除問卷用
	public List<Questionnaire> findByIdIn(List<Integer> idList);

	// 模糊搜尋用 Containing當字串帶""時，才會帶全部資料，若null，則為0筆。
	public List<Questionnaire> findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String title,
			LocalDate startDate, LocalDate endDate);

	public List<Questionnaire> findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
			String title, LocalDate startDate, LocalDate endDate);

	// =========================================SQL語法教學=====================================================
	// ======================================== insert 新增 ===================================================
	// 使用JPQL，可以在定義方法上，直接寫語法，可小寫
	// questionnaire(放DB內的欄位名稱)
	// values(:對應@Param的字段)
	// 在語法上使用insert、update，1.必需要加@Modifying、@Transactional的限制 2.insert要加是否使用native
	// query(其預設為false)
	// 因太長切成兩個字段，後面要加nativeQuery = true時，需要加value =，否則會報錯
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO questionnaire(title, description, is_published, start_date, end_date) "
			+ " VALUES(:title, :desp, :isPublished, :startDate, :endDate)", nativeQuery = true)
	public int insert(@Param("title") String title, @Param("desp") String description,
			@Param("isPublished") boolean isPublished, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate); // @Param("字串可自定義")

	// 簡易寫法
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO questionnaire(title, description, is_published, start_date, end_date) "
			+ " VALUES(?1, ?2, ?3, ?4, ?5)", nativeQuery = true) // 對應位置
	public int insertDate(String title, String description, boolean isPublished, LocalDate startDate, LocalDate endDate); // @Param("字串可自定義")
	// ========================================== update 修改 ====================================================
	@Modifying
	@Transactional
	@Query(value = "UPDATE questionnaire SET title = :title, description = :desp "
			+ " WHERE id = :id", nativeQuery = true)
	public int update(
			@Param("id") int id, //
			@Param("title") String title, //
			@Param("desp") String description);

	// update不使用nativeQuery的語法
	/**
	 * 
	 * 不寫nativeQuery = true 等同為預設false \n 語法中表的名稱要變成entity的class名稱：欄位名稱要變成屬性名稱
	 * clearAutomatically = true: 清除持久化上下文，即清除暫存資料 (情境：第一次撈資料 => 資料update
	 * =>第二次撈資料，卻是撈到未更新的資料，表示此資料已暫存，故需清除暫存才能撈到更新後的資料。
	 */
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "UPDATE Questionnaire SET title = :title, description = :desp, startDate = :startDate "
			+ " WHERE id = :id") // 不寫nativeQuery = true 等同為預設false，等於不透過資料表操作，而是透過entity
									// class的名稱：Questionnaire進行操作
	public int updateData(
			@Param("id") int id, //
			@Param("title") String title, //
			@Param("desp") String description, //
			@Param("startDate") LocalDate startDate);

	// ======================================= select 查詢 =======================================================
	@Query(value = "SELECT * FROM questionnaire " + " WHERE start_date > :startDate", nativeQuery = true)
	public List<Questionnaire> findByStartDate(@Param("startDate") LocalDate startDate);

	@Query(value = "SELECT new Questionnaire(id, title, description, published, startDate, endDate) "
			+ " FROM Questionnaire WHERE startDate > :startDate")
	public List<Questionnaire> findByStartDate1(@Param("startDate") LocalDate startDate);

	// nativeQuery = false，select欄位要使用建構方法的方式，且entity中也要有對應的建構方法
	@Query(value = "SELECT new Questionnaire(id, title, published) " // nativeQuery =
																		// false時，要只撈出幾個欄位的資料，就必須在entity中有包含這些參數的建構方法
			+ " FROM Questionnaire WHERE startDate > :startDate")
	public List<Questionnaire> findByStartDate2(@Param("startDate") LocalDate startDate);

	// 使用別名，語法 as 別名
	@Query(value = "SELECT qu " + " FROM Questionnaire AS qu WHERE startDate > :startDate or published = :isPublished")
	public List<Questionnaire> findByStartDate3(@Param("startDate") LocalDate startDate,
			@Param("isPublished") boolean published);

	// orderby
	@Query(value = "SELECT qu FROM Questionnaire AS qu "
			+ " WHERE startDate > :startDate or published = :isPublished ORDER BY id DESC")
	public List<Questionnaire> findByStartDate4(
			@Param("startDate") LocalDate startDate, //
			@Param("isPublished") boolean published);

	// orderby + limit
	// 1. limit語法只能使用在nativeQuery = true
	// 2. limit要放在語法的最後
	@Query(value = "SELECT * FROM questionnaire AS qu "
			+ " WHERE start_date > :startDate or is_published = :isPublished ORDER BY id DESC LIMIT :num", nativeQuery = true)
	public List<Questionnaire> findByStartDate5(
			@Param("startDate") LocalDate startDate, //
			@Param("isPublished") boolean published, //
			@Param("num") int limitNum);

	// limit (可帶入index)
	@Query(value = "SELECT * FROM questionnaire "
			+ " LIMIT :startIndex, :limitNum", nativeQuery = true)
	public List<Questionnaire> findWithLimitAndStartIndex(
			@Param("startIndex") int startIndex, //
			@Param("limitNum") int limitNum);
	
	// like模糊查詢
	@Query(value = "SELECT * FROM questionnaire "
			+ " WHERE title LIKE %:title%", nativeQuery = true)
	public List<Questionnaire> searchTitleLike(@Param("title") String title);
	
	// 用regexp 正則表達式 SQL語法支援
	@Query(value = "SELECT * FROM questionnaire "
			+ " WHERE title regexp :title", nativeQuery = true)
	public List<Questionnaire> searchTitleLike2(@Param("title") String title);
	
	// regexp or
	// regexp只能用在nativeQuery = true
	@Query(value = "SELECT * FROM questionnaire "
			+ " WHERE description regexp :keyword1 | :keyword2", nativeQuery = true)
	public List<Questionnaire> searchDescriptionContaining(
			@Param("keyword1") String keyword1, //
			@Param("keyword2") String keyword2);
	
	// regexp or (使用concat串接，效果同上）
	@Query(value = "SELECT * FROM questionnaire "
			+ " WHERE description regexp concat(:keyword1, '|', :keyword2)", nativeQuery = true)
	public List<Questionnaire> searchDescriptionContaining2(
			@Param("keyword1") String keyword1, //
			@Param("keyword2") String keyword2);
	
	// join
	@Query("SELECT new com.example.questionnaire.vo.QnQuVo(" //因為沒有給予託管，new之後直接給予路徑
			+ " qn.id, qn.title, qn.description, qn.published, qn.startDate, qn.endDate,"
			+ " q.quId, q.qnTitle, q.optionType, q.necessary, q.option) "
			+ " FROM Questionnaire AS qn JOIN Question AS q ON qn.id = q.qnId")
	public List<QnQuVo> selectJoinQnQu();
	
	
	@Query("SELECT new com.example.questionnaire.vo.QnQuVo("
			+ " qn.id, qn.title, qn.description, qn.published, qn.startDate, qn.endDate,"
			+ " q.quId, q.qnTitle, q.optionType, q.necessary, q.option) "
			+ " FROM Questionnaire AS qn JOIN Question AS q ON qn.id = q.qnId"
			+ " WHERE qn.title like %:title% and qn.startDate >= :startDate and qn.endDate <= :endDate")
	public List<QnQuVo> selectFuzzy(
			@Param("title") String title, //
			@Param("startDate") LocalDate startDate, //
			@Param("endDate")LocalDate endDate);
}
