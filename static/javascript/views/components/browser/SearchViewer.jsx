/**
 * Sherlock Search Viewer component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description : Displays search results.
 **/

 import React from 'react';

 const SearchViewer = ({ searchResults, onSearchResultClicked }) => {
     if (searchResults.length > 0) {
         return (
             <div>
             {
                 searchResults.map((result, i) => {
                     return (
                         <div key={i}
                             onClick={ e => {
                                 onSearchResultClicked(result.path.split("\\"));
                             }}
                             className="file-browser-cell">
                                 <div className="file-browser-cell-path">
                                    { result.path }
                                 </div>
                                 <div>
                                     <table className="file-browser-cell-frags">
                                         <tbody>
                                         {
                                             result.frags.map((frag, j) => {
                                                 return (
                                                     <tr onClick={e => {
                                                         e.stopPropagation();
                                                         onSearchResultClicked(result.path.split("\\"), parseInt(frag.line));
                                                     }} className="file-browser-cell-frag" key={j}>
                                                         <td className="file-browser-cell-line">
                                                              <div>{ frag.line }</div>
                                                         </td>
                                                         <td className="file-browser-cell-text">
                                                             <div dangerouslySetInnerHTML={{ __html: frag.frag }}></div>
                                                         </td>
                                                     </tr>
                                                 )
                                             })
                                         }
                                         </tbody>
                                    </table>
                               </div>
                         </div>
                     );
                 })
             }
             </div>
         );
     }

     return (
        <div className="file-browser-placeholder">No Results Found</div> 
     );
 };

 export default SearchViewer;
