package cas.custom.component;

/**
 * 
 * 用于自定义Session的前置、后置处理
 * 
 * @author ChengPan
 *
 */
public interface CustomSessionProcessor {
	
	/**
	 * 用于自定义Session的初始化工作，例如从缓存中加载已有的Session属性
	 */
	void initialize();
	
	/**
	 * 用于自定义Session的后置处理工作，例如在请求处理完成之后，将自定义Session的属性提交同步到缓存中
	 */
	void commit();
}
